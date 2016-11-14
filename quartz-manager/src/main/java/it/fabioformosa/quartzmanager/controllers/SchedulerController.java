package it.fabioformosa.quartzmanager.controllers;

import javax.annotation.Resource;

import org.quartz.DailyTimeIntervalTrigger;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.triggers.SimpleTriggerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import it.fabioformosa.quartzmanager.dto.SchedulerConfigParam;
import it.fabioformosa.quartzmanager.dto.TriggerProgress;
import it.fabioformosa.quartzmanager.scheduler.TriggerMonitor;

@RestController
@RequestMapping("/scheduler")
public class SchedulerController {

	private static final int MILLS_IN_A_DAY = 1000 * 60 * 60 * 24;
	private static final int SEC_IN_A_DAY = 60 * 60 * 24;

	private final Logger log = LoggerFactory.getLogger(SchedulerController.class);

	@Resource
	private Scheduler scheduler;

	@Resource
	private TriggerMonitor triggerMonitor;

	@SuppressWarnings("unused")
	private long fromMillsIntervalToTriggerPerDay(long repeatIntervalInMills) {
		return (int) Math.ceil(MILLS_IN_A_DAY / repeatIntervalInMills);
	}

	private int fromTriggerPerDayToMillSecInterval(long triggerPerDay) {
		return (int) Math.ceil(Long.valueOf(MILLS_IN_A_DAY) / triggerPerDay); //with ceil the triggerPerDay is a max value
	}

	private int fromTriggerPerDayToSecInterval(long triggerPerDay) {
		return (int) Math.ceil(Long.valueOf(SEC_IN_A_DAY) / triggerPerDay);
	}

	@RequestMapping(value = "/config", method = RequestMethod.GET)
	public SchedulerConfigParam getConfig() {
		SchedulerConfigParam config = new SchedulerConfigParam();

		int maxCount = 0;
		long repeatIntervalInMills = 0;
		if (triggerMonitor.getTrigger() instanceof SimpleTrigger) {
			SimpleTrigger simpleTrigger = (SimpleTrigger) triggerMonitor.getTrigger();
			maxCount = simpleTrigger.getRepeatCount() + 1;
			repeatIntervalInMills = fromTriggerPerDayToMillSecInterval(simpleTrigger.getRepeatInterval());
		} else if (triggerMonitor.getTrigger() instanceof DailyTimeIntervalTrigger) {
			DailyTimeIntervalTrigger dailyTimeIntervalTrigger = (DailyTimeIntervalTrigger) triggerMonitor
					.getTrigger();
			maxCount = dailyTimeIntervalTrigger.getRepeatCount() + 1;
			repeatIntervalInMills = fromTriggerPerDayToSecInterval(
					dailyTimeIntervalTrigger.getRepeatInterval());
		}

		config.setMaxCount(maxCount);
		config.setTriggerPerDay(repeatIntervalInMills);
		return config;
	}

	@RequestMapping("/progress")
	public TriggerProgress getProgressInfo() throws SchedulerException {

		SimpleTriggerImpl jobTrigger = ((SimpleTriggerImpl) scheduler
				.getTrigger(triggerMonitor.getTrigger().getKey()));

		TriggerProgress progress = new TriggerProgress();
		if (jobTrigger != null && jobTrigger.getJobKey() != null) {
			progress.setJobKey(jobTrigger.getJobKey().getName());
			progress.setJobClass(jobTrigger.getClass().getSimpleName());
			progress.setTimesTriggered(jobTrigger.getTimesTriggered());
			progress.setRepeatCount(jobTrigger.getRepeatCount());
			progress.setFinalFireTime(jobTrigger.getFinalFireTime());
			progress.setNextFireTime(jobTrigger.getNextFireTime());
			progress.setPreviousFireTime(jobTrigger.getPreviousFireTime());
		}
		return progress;
	}

	@RequestMapping("/pause")
	public void pause() throws SchedulerException {
		scheduler.standby();
	}

	@RequestMapping(value = "/config", method = RequestMethod.POST)
	public SchedulerConfigParam postConfig(@RequestBody SchedulerConfigParam config)
			throws SchedulerException {

		SimpleTrigger trigger = (SimpleTrigger) triggerMonitor.getTrigger();
		TriggerBuilder<SimpleTrigger> triggerBuilder = trigger.getTriggerBuilder();

		int intervalInSeconds = fromTriggerPerDayToMillSecInterval(config.getTriggerPerDay());
		Trigger newTrigger = triggerBuilder.withSchedule(SimpleScheduleBuilder.simpleSchedule()
				.withIntervalInMilliseconds(intervalInSeconds).withRepeatCount(config.getMaxCount() - 1))
				.build();

		scheduler.rescheduleJob(triggerMonitor.getTrigger().getKey(), newTrigger);
		triggerMonitor.setTrigger(newTrigger);
		return config;
	}

	@RequestMapping("/resume")
	public void resume() throws SchedulerException {
		scheduler.start();
	}

	@RequestMapping("/run")
	public void run() throws SchedulerException {
		log.info("Starting scheduler...");
		scheduler.start();
	}

	@RequestMapping("/stop")
	public void stop() throws SchedulerException {
		log.info("Stopping scheduler...");
		scheduler.shutdown(true);
	}

}
