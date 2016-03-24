package it.fabioformosa.quartzmanager.controllers;

import it.fabioformosa.quartzmanager.dto.SchedulerConfigParam;
import it.fabioformosa.quartzmanager.dto.TriggerProgress;

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

@RestController
@RequestMapping("/scheduler")
public class SchedulerController {

	private static final int MILLS_IN_A_DAY = 1000 * 60 * 60 * 24;

	private final Logger log = LoggerFactory
			.getLogger(SchedulerController.class);

	@Resource
	private Scheduler scheduler;

	@Resource
	private SimpleTrigger jobTrigger = null;

	private long fromMillsIntervalToTriggerPerDay(long repeatIntervalInMills) {
		return Math.round(MILLS_IN_A_DAY / repeatIntervalInMills);
	}

	private int fromTriggerPerDayToSecInterval(long triggerPerDay) {
		return Math.round((MILLS_IN_A_DAY / triggerPerDay) / 1000);
	}

	@RequestMapping(value = "/config", method = RequestMethod.GET)
	public SchedulerConfigParam getConfig() {
		SchedulerConfigParam config = new SchedulerConfigParam();
		config.setMaxCount(jobTrigger.getRepeatCount());
		long repeatIntervalInMills = jobTrigger.getRepeatInterval();
		config.setTriggerPerDay(fromMillsIntervalToTriggerPerDay(repeatIntervalInMills));
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
			@RequestBody SchedulerConfigParam config) throws SchedulerException {

		TriggerBuilder<SimpleTrigger> triggerBuilder = jobTrigger
				.getTriggerBuilder();

		int intervalInSeconds = fromTriggerPerDayToSecInterval(config
				.getTriggerPerDay());
		Trigger newTrigger = triggerBuilder.withSchedule(
				SimpleScheduleBuilder.simpleSchedule()
						.withIntervalInSeconds(intervalInSeconds)
						.withRepeatCount(config.getMaxCount())).build();

		scheduler.rescheduleJob(jobTrigger.getKey(), newTrigger);
		this.jobTrigger = (SimpleTrigger) newTrigger;
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
