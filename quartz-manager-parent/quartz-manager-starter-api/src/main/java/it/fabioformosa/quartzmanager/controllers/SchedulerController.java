package it.fabioformosa.quartzmanager.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.fabioformosa.quartzmanager.dto.SchedulerConfigParam;
import it.fabioformosa.quartzmanager.dto.SchedulerDTO;
import it.fabioformosa.quartzmanager.dto.TriggerStatus;
import it.fabioformosa.quartzmanager.enums.SchedulerStates;
import it.fabioformosa.quartzmanager.services.LegacySchedulerService;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.impl.triggers.SimpleTriggerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
@SecurityRequirement(name = "basic-auth")
@RequestMapping("/quartz-manager/scheduler")
public class SchedulerController {

  private final Logger log = LoggerFactory.getLogger(SchedulerController.class);

  private LegacySchedulerService legacySchedulerService;

  public SchedulerController(LegacySchedulerService legacySchedulerService, ConversionService conversionService) {
    this.legacySchedulerService = legacySchedulerService;
    this.conversionService = conversionService;
  }

  @Resource
  private ConversionService conversionService;

  //TODO replace this a list of trigger
  @GetMapping("/config")
  @Operation(summary = "Get the config of the trigger")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Return the trigger config",
      content = { @Content(mediaType = "application/json",
        schema = @Schema(implementation = SchedulerConfigParam.class)) })
  })
  public SchedulerConfigParam getConfig() throws SchedulerException {
    log.debug("SCHEDULER - GET CONFIG params");
    SchedulerConfigParam schedulerConfigParam = legacySchedulerService.getOneSimpleTrigger()
      .map(SchedulerController::fromSimpleTriggerToSchedulerConfigParam)
      .orElse(new SchedulerConfigParam(0L, 0, 0));
    return schedulerConfigParam;
  }

  public static SchedulerConfigParam fromSimpleTriggerToSchedulerConfigParam(SimpleTrigger simpleTrigger){
    int timesTriggered = simpleTrigger.getTimesTriggered();
    int maxCount = simpleTrigger.getRepeatCount() + 1;
    long triggersPerDay = LegacySchedulerService.fromMillsIntervalToTriggerPerDay(simpleTrigger.getRepeatInterval());
    return new SchedulerConfigParam(triggersPerDay, maxCount, timesTriggered);
  }

  @GetMapping
  @Operation(summary = "Get the scheduler details")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Return the scheduler config",
      content = { @Content(mediaType = "application/json",
        schema = @Schema(implementation = SchedulerDTO.class)) })
  })
  public SchedulerDTO getScheduler() {
    log.debug("SCHEDULER - GET Scheduler...");
    SchedulerDTO schedulerDTO = conversionService.convert(legacySchedulerService.getScheduler(), SchedulerDTO.class);
    return schedulerDTO;
  }

  //TODO move this to the Trigger Controller
  @GetMapping("/progress")
  @Operation(summary = "Get the trigger status")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Return the trigger status",
      content = { @Content(mediaType = "application/json",
        schema = @Schema(implementation = TriggerStatus.class)) })
  })
  public TriggerStatus getProgressInfo() throws SchedulerException {
    log.trace("SCHEDULER - GET PROGRESS INFO");
    TriggerStatus progress = new TriggerStatus();

    SimpleTriggerImpl jobTrigger = (SimpleTriggerImpl) legacySchedulerService.getOneSimpleTrigger().get();
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
  @Operation(summary = "Get the scheduler status")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Return the scheduler status",
      content = { @Content(mediaType = "application/json",
        schema = @Schema(implementation = SchedulerStates.class)) })
  })
  public Map<String, String> getStatus() throws SchedulerException {
    log.trace("SCHEDULER - GET STATUS");
    String schedulerState = "";
    if (legacySchedulerService.getScheduler().isShutdown() || !legacySchedulerService.getScheduler().isStarted())
      schedulerState = SchedulerStates.STOPPED.toString();
    else if (legacySchedulerService.getScheduler().isStarted() && legacySchedulerService.getScheduler().isInStandbyMode())
      schedulerState = SchedulerStates.PAUSED.toString();
    else
      schedulerState = SchedulerStates.RUNNING.toString();
    return Collections.singletonMap("data", schedulerState.toLowerCase());
  }

  @GetMapping("/pause")
  @Operation(summary = "Get paused the scheduler")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "204", description = "Got paused successfully")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void pause() throws SchedulerException {
    log.info("SCHEDULER - PAUSE COMMAND");
    legacySchedulerService.getScheduler().standby();
  }

  @GetMapping("/resume")
  @Operation(summary = "Get resumed the scheduler")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "204", description = "Got resumed successfully")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void resume() throws SchedulerException {
    log.info("SCHEDULER - RESUME COMMAND");
    legacySchedulerService.getScheduler().start();
  }

  @GetMapping("/run")
  @Operation(summary = "Start the scheduler")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "204", description = "Got started successfully")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void run() throws SchedulerException {
    log.info("SCHEDULER - START COMMAND");
    legacySchedulerService.getScheduler().start();
  }

  @GetMapping("/stop")
  @Operation(summary = "Stop the scheduler")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "204", description = "Got stopped successfully")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void stop() throws SchedulerException {
    log.info("SCHEDULER - STOP COMMAND");
    legacySchedulerService.getScheduler().shutdown(true);
  }

}
