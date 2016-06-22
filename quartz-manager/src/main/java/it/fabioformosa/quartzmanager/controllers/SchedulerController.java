package it.fabioformosa.quartzmanager.controllers;

import javax.annotation.Resource;

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

@RestController
@RequestMapping("/scheduler")
public class SchedulerController {

	private static final int MILLS_IN_A_DAY = 1000 * 60 * 60 * 24;
	private static final int SEC_IN_A_DAY = 60 * 60 * 24;

	private final Logger log = LoggerFactory
			.getLogger(SchedulerController.class);

	@Resource
	private Scheduler scheduler;

	@Resource
	private SimpleTrigger jobTrigger = null;

	private long fromMillsIntervalToTriggerPerDay(long repeatIntervalInMills) {
		return (int) Math.ceil(MILLS_IN_A_DAY / repeatIntervalInMills);
	}

	private int fromTriggerPerDayToMillSecInterval(long triggerPerDay) {
		return (int) Math.ceil(Long.valueOf(MILLS_IN_A_DAY) / triggerPerDay); //with ceil the triggerPerDay is a max value
	}

	@SuppressWarnings("unused")
	private int fromTriggerPerDayToSecInterval(long triggerPerDay) {
		return (int) Math.ceil(Long.valueOf(SEC_IN_A_DAY) / triggerPerDay);
	}

	@RequestMapping(value = "/config", method = RequestMethod.GET)
	public SchedulerConfigParam getConfig() {
		SchedulerConfigParam config = new SchedulerConfigParam();
		config.setMaxCount(jobTrigger.getRepeatCount() + 1);
		long repeatIntervalInMills = jobTrigger.getRepeatInterval();
		config.setTriggerPerDay(
				fromMillsIntervalToTriggerPerDay(repeatIntervalInMills));
		return config;
	}

	@RequestMapping("/progress")
	public TriggerProgress getProgressInfo() throws SchedulerException {

		SimpleTriggerImpl jobTrigger = ((SimpleTriggerImpl) scheduler
				.getTrigger(this.jobTrigger.getKey()));

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
	public SchedulerConfigParam postConfig(
			@RequestBody SchedulerConfigParam config)
			throws SchedulerException {

		TriggerBuilder<SimpleTrigger> triggerBuilder = jobTrigger
				.getTriggerBuilder();

		int intervalInSeconds = fromTriggerPerDayToMillSecInterval(
				config.getTriggerPerDay());
		Trigger newTrigger = triggerBuilder
				.withSchedule(SimpleScheduleBuilder.simpleSchedule()
						.withIntervalInMilliseconds(intervalInSeconds)
						.withRepeatCount(config.getMaxCount() - 1))
				.build();

		scheduler.rescheduleJob(jobTrigger.getKey(), newTrigger);
		jobTrigger = (SimpleTrigger) newTrigger;
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
