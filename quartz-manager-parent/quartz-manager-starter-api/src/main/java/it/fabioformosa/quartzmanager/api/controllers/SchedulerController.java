package it.fabioformosa.quartzmanager.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.fabioformosa.quartzmanager.api.dto.SchedulerDTO;
import it.fabioformosa.quartzmanager.api.services.SchedulerService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static it.fabioformosa.quartzmanager.api.common.config.OpenAPIConfigConsts.QUARTZ_MANAGER_SEC_OAS_SCHEMA;
import static it.fabioformosa.quartzmanager.api.common.config.QuartzManagerPaths.QUARTZ_MANAGER_BASE_CONTEXT_PATH;

/**
 * This controller provides scheduler info about config and status. It provides
 * also methods to set new config and start/stop/resume the scheduler.
 *
 * @author Fabio.Formosa
 */
@Slf4j
@RestController
@SecurityRequirement(name = QUARTZ_MANAGER_SEC_OAS_SCHEMA)
@RequestMapping(SchedulerController.SCHEDULER_CONTROLLER_BASE_URL)
public class SchedulerController {

  protected static final String SCHEDULER_CONTROLLER_BASE_URL = QUARTZ_MANAGER_BASE_CONTEXT_PATH + "/scheduler";

  private final SchedulerService schedulerService;

  public SchedulerController(SchedulerService schedulerService) {
    this.schedulerService = schedulerService;
  }

  @GetMapping
  @Operation(summary = "Get the scheduler details")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Return the scheduler config",
      content = { @Content(mediaType = "application/json",
        schema = @Schema(implementation = SchedulerDTO.class)) })
  })
  public SchedulerDTO getScheduler() {
    log.trace("SCHEDULER - GET Scheduler...");
    return schedulerService.getScheduler();
  }

  @PostMapping("/standby")
  @Operation(summary = "Put the scheduler in standby mode")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "204", description = "Scheduler moved to standby successfully")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void standby() throws SchedulerException {
    log.info("SCHEDULER - STANDBY COMMAND");
    schedulerService.standby();
  }

  @PostMapping("/resume")
  @Operation(summary = "Resume the scheduler from standby mode")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "204", description = "Scheduler resumed successfully")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void resume() throws SchedulerException {
    log.info("SCHEDULER - RESUME COMMAND");
    schedulerService.start();
  }

  @PostMapping("/start")
  @Operation(summary = "Start the scheduler")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "204", description = "Scheduler started successfully")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void start() throws SchedulerException {
    log.info("SCHEDULER - START COMMAND");
    schedulerService.start();
  }

  @PostMapping("/start-delayed/{seconds}")
  @Operation(summary = "Start the scheduler after a delay")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "204", description = "Scheduler delayed start scheduled successfully")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void startDelayed(@PathVariable int seconds) throws SchedulerException {
    log.info("SCHEDULER - START DELAYED COMMAND: {} seconds", seconds);
    schedulerService.startDelayed(seconds);
  }

  @PostMapping("/pause-all")
  @Operation(summary = "Pause all scheduler triggers")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "204", description = "All triggers paused successfully")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void pauseAll() throws SchedulerException {
    log.info("SCHEDULER - PAUSE ALL COMMAND");
    schedulerService.pauseAll();
  }

  @DeleteMapping
  @Operation(summary = "Clear all scheduler data")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "204", description = "Scheduler cleared successfully")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void clear() throws SchedulerException {
    log.info("SCHEDULER - CLEAR COMMAND");
    schedulerService.clear();
  }

  @PostMapping("/shutdown")
  @Operation(summary = "Shutdown the scheduler terminally")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "204", description = "Scheduler shut down successfully")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void shutdown() throws SchedulerException {
    log.info("SCHEDULER - SHUTDOWN COMMAND");
    schedulerService.shutdown();
  }

}
