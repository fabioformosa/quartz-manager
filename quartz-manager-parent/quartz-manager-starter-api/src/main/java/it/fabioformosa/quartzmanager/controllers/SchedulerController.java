package it.fabioformosa.quartzmanager.controllers;

import java.util.Collections;
import java.util.Map;

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
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import it.fabioformosa.quartzmanager.dto.SchedulerConfigParam;
import it.fabioformosa.quartzmanager.dto.TriggerStatus;
import it.fabioformosa.quartzmanager.enums.SchedulerStates;
import it.fabioformosa.quartzmanager.scheduler.TriggerMonitor;

/**
 * This controller provides scheduler info about config and status. It provides
 * also methods to set new config and start/stop/resume the scheduler.
 *
 * @author Fabio.Formosa
 *
 */
@RestController
@RequestMapping("/quartz-manager/scheduler")
@Api(value = "scheduler")
public class SchedulerController {

    private static final int MILLS_IN_A_DAY = 1000 * 60 * 60 * 24;
    private static final int SEC_IN_A_DAY = 60 * 60 * 24;

    private final Logger log = LoggerFactory.getLogger(SchedulerController.class);

    @Resource
    private Scheduler scheduler;

    @Resource
    private TriggerMonitor triggerMonitor;

    private long fromMillsIntervalToTriggerPerDay(long repeatIntervalInMills) {
        return (int) Math.ceil(MILLS_IN_A_DAY / repeatIntervalInMills);
    }

    private int fromTriggerPerDayToMillsInterval(long triggerPerDay) {
        return (int) Math.ceil(Long.valueOf(MILLS_IN_A_DAY) / triggerPerDay); // with ceil the triggerPerDay is a max value
    }

    @SuppressWarnings("unused")
    private int fromTriggerPerDayToSecInterval(long triggerPerDay) {
        return (int) Math.ceil(Long.valueOf(SEC_IN_A_DAY) / triggerPerDay);
    }

    @GetMapping("/config")
    public SchedulerConfigParam getConfig() throws SchedulerException {
        log.debug("SCHEDULER - GET CONFIG params");

        SimpleTrigger jobTrigger = (SimpleTrigger) scheduler.getTrigger(triggerMonitor.getTrigger().getKey());
        int maxCount = jobTrigger.getRepeatCount() + 1;
        long triggersPerDay = fromMillsIntervalToTriggerPerDay(jobTrigger.getRepeatInterval());

        return new SchedulerConfigParam(triggersPerDay, maxCount);
    }

    @GetMapping("/progress")
    public TriggerStatus getProgressInfo() throws SchedulerException {
        log.trace("SCHEDULER - GET PROGRESS INFO");
        TriggerStatus progress = new TriggerStatus();

        SimpleTriggerImpl jobTrigger = (SimpleTriggerImpl) scheduler.getTrigger(triggerMonitor.getTrigger().getKey());
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

    @GetMapping(produces = "application/json")
    public Map<String, String> getStatus() throws SchedulerException {
        log.trace("SCHEDULER - GET STATUS");
        String schedulerState = "";
        if (scheduler.isShutdown() || !scheduler.isStarted())
            schedulerState = SchedulerStates.STOPPED.toString();
        else if (scheduler.isStarted() && scheduler.isInStandbyMode())
            schedulerState = SchedulerStates.PAUSED.toString();
        else
            schedulerState = SchedulerStates.RUNNING.toString();
        return Collections.singletonMap("data", schedulerState.toLowerCase());
    }

    @GetMapping("/pause")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void pause() throws SchedulerException {
        log.info("SCHEDULER - PAUSE COMMAND");
        scheduler.standby();
    }

    @PostMapping("/config")
    public SchedulerConfigParam postConfig(@RequestBody SchedulerConfigParam config) throws SchedulerException {
        log.info("SCHEDULER - NEW CONFIG {}", config);
        SimpleTrigger trigger = (SimpleTrigger) triggerMonitor.getTrigger();

        TriggerBuilder<SimpleTrigger> triggerBuilder = trigger.getTriggerBuilder();

        int intervalInMills = fromTriggerPerDayToMillsInterval(config.getTriggerPerDay());
        Trigger newTrigger = triggerBuilder.withSchedule(SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInMilliseconds(intervalInMills).withRepeatCount(config.getMaxCount() - 1)).build();

        scheduler.rescheduleJob(triggerMonitor.getTrigger().getKey(), newTrigger);
        triggerMonitor.setTrigger(newTrigger);
        return config;
    }

    @GetMapping("/resume")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resume() throws SchedulerException {
        log.info("SCHEDULER - RESUME COMMAND");
        scheduler.start();
    }

    @GetMapping("/run")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void run() throws SchedulerException {
        log.info("SCHEDULER - START COMMAND");
        scheduler.start();
    }

    @GetMapping("/stop")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void stop() throws SchedulerException {
        log.info("SCHEDULER - STOP COMMAND");
        scheduler.shutdown(true);
    }

}
