package it.fabioformosa.quartzmanager.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.fabioformosa.quartzmanager.dto.SchedulerConfigParam;
import it.fabioformosa.quartzmanager.dto.TriggerDTO;
import it.fabioformosa.quartzmanager.exceptions.TriggerNotFoundException;
import it.fabioformosa.quartzmanager.services.LegacySchedulerService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RequestMapping(TriggerController.TRIGGER_CONTROLLER_BASE_URL)
@SecurityRequirement(name = "basic-auth")
@RestController
public class TriggerController extends AbstractTriggerController {

  static public final String TRIGGER_CONTROLLER_BASE_URL = "/quartz-manager/triggers";
  static public final String SIMPLE_TRIGGER_BASE_URL = "/simple-triggers";

  private LegacySchedulerService schedulerService;

  public TriggerController(LegacySchedulerService schedulerService) {
    this.schedulerService = schedulerService;
  }

  @GetMapping("/{name}")
  public TriggerDTO getTrigger(@PathVariable String name) throws SchedulerException, TriggerNotFoundException {
    return schedulerService.getLegacyTriggerByName(name);
  }

  @Deprecated
  @PostMapping("/{name}")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Create a new trigger")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "201", description = "Created the new trigger",
      content = { @Content(mediaType = "application/json",
        schema = @Schema(implementation = TriggerDTO.class)) }),
    @ApiResponse(responseCode = "400", description = "Invalid config supplied",
      content = @Content)
  })
  public TriggerDTO postTrigger(@PathVariable String name, @Valid @RequestBody SchedulerConfigParam config) throws SchedulerException, ClassNotFoundException {
    log.info("TRIGGER - CREATING a trigger {} {}", name, config);
    TriggerDTO newTriggerDTO = schedulerService.scheduleNewTrigger(name, jobClassname, config);
    log.info("TRIGGER - CREATED a trigger {}", newTriggerDTO);
    return newTriggerDTO;
  }



  @PutMapping("/{name}")
  @Operation(summary = "Reschedule the trigger")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Rescheduled the trigger",
      content = { @Content(mediaType = "application/json",
        schema = @Schema(implementation = TriggerDTO.class)) }),
    @ApiResponse(responseCode = "400", description = "Invalid config supplied",
      content = @Content)
  })
  public TriggerDTO rescheduleTrigger(@PathVariable String name, @Valid @RequestBody SchedulerConfigParam config) throws SchedulerException {
    log.info("TRIGGER - RESCHEDULING the trigger {} {}", name, config);
    TriggerDTO triggerDTO = schedulerService.rescheduleTrigger(name, config);
    log.info("TRIGGER - RESCHEDULED the trigger {}", triggerDTO);
    return triggerDTO;
  }


}
