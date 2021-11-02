package it.fabioformosa.quartzmanager.controllers;

import io.swagger.annotations.Api;
import it.fabioformosa.quartzmanager.dto.SchedulerConfigParam;
import it.fabioformosa.quartzmanager.dto.SchedulerDTO;
import it.fabioformosa.quartzmanager.dto.TriggerStatus;
import it.fabioformosa.quartzmanager.enums.SchedulerStates;
import it.fabioformosa.quartzmanager.services.SchedulerService;
import org.quartz.*;
import org.quartz.impl.triggers.SimpleTriggerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Map;

/**
 * This controller provides scheduler info about config and status. It provides
 * also methods to set new config and start/stop/resume the scheduler.
 *
 * @author Fabio.Formosa
 */
@RestController
@RequestMapping("/quartz-manager/scheduler")
@Api(value = "scheduler")
public class SchedulerController {

  private final Logger log = LoggerFactory.getLogger(SchedulerController.class);

  private SchedulerService schedulerService;

  public SchedulerController(SchedulerService schedulerService, ConversionService conversionService) {
    this.schedulerService = schedulerService;
    this.conversionService = conversionService;
  }

  @Resource
  private ConversionService conversionService;

  @GetMapping("/config")
  public SchedulerConfigParam getConfig() throws SchedulerException {
    log.debug("SCHEDULER - GET CONFIG params");
    SchedulerConfigParam schedulerConfigParam = schedulerService.getOneSimpleTrigger()
      .map(SchedulerController::fromSimpleTriggerToSchedulerConfigParam)
      .orElse(new SchedulerConfigParam(0, 0, 0));
    return schedulerConfigParam;
  }

  public static SchedulerConfigParam fromSimpleTriggerToSchedulerConfigParam(SimpleTrigger simpleTrigger){
    int timesTriggered = simpleTrigger.getTimesTriggered();
    int maxCount = simpleTrigger.getRepeatCount() + 1;
    long triggersPerDay = SchedulerService.fromMillsIntervalToTriggerPerDay(simpleTrigger.getRepeatInterval());
    return new SchedulerConfigParam(triggersPerDay, maxCount, timesTriggered);
  }

  @GetMapping
  public SchedulerDTO getScheduler() {
    log.debug("SCHEDULER - GET Scheduler...");
    SchedulerDTO schedulerDTO = conversionService.convert(schedulerService.getScheduler(), SchedulerDTO.class);
    return schedulerDTO;
  }

  @GetMapping("/progress")
  public TriggerStatus getProgressInfo() throws SchedulerException {
    log.trace("SCHEDULER - GET PROGRESS INFO");
    TriggerStatus progress = new TriggerStatus();

    SimpleTriggerImpl jobTrigger = (SimpleTriggerImpl) schedulerService.getOneSimpleTrigger().get();
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

  @GetMapping(value = "/status", produces = "application/json")
  public Map<String, String> getStatus() throws SchedulerException {
    log.trace("SCHEDULER - GET STATUS");
    String schedulerState = "";
    if (schedulerService.getScheduler().isShutdown() || !schedulerService.getScheduler().isStarted())
      schedulerState = SchedulerStates.STOPPED.toString();
    else if (schedulerService.getScheduler().isStarted() && schedulerService.getScheduler().isInStandbyMode())
      schedulerState = SchedulerStates.PAUSED.toString();
    else
      schedulerState = SchedulerStates.RUNNING.toString();
    return Collections.singletonMap("data", schedulerState.toLowerCase());
  }

  @GetMapping("/pause")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void pause() throws SchedulerException {
    log.info("SCHEDULER - PAUSE COMMAND");
    schedulerService.getScheduler().standby();
  }

  @GetMapping("/resume")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void resume() throws SchedulerException {
    log.info("SCHEDULER - RESUME COMMAND");
    schedulerService.getScheduler().start();
  }

  @GetMapping("/run")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void run() throws SchedulerException {
    log.info("SCHEDULER - START COMMAND");
    schedulerService.getScheduler().start();
  }

  @GetMapping("/stop")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void stop() throws SchedulerException {
    log.info("SCHEDULER - STOP COMMAND");
    schedulerService.getScheduler().shutdown(true);
  }

}
