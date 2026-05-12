package it.fabioformosa.quartzmanager.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.fabioformosa.quartzmanager.api.dto.TriggerKeyDTO;
import it.fabioformosa.quartzmanager.api.dto.TriggerDTO;
import it.fabioformosa.quartzmanager.api.dto.TriggerInputDTO;
import it.fabioformosa.quartzmanager.api.exceptions.TriggerNotFoundException;
import it.fabioformosa.quartzmanager.api.services.TriggerService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.List;

import static it.fabioformosa.quartzmanager.api.common.config.OpenAPIConfigConsts.QUARTZ_MANAGER_SEC_OAS_SCHEMA;
import static it.fabioformosa.quartzmanager.api.common.config.QuartzManagerPaths.QUARTZ_MANAGER_BASE_CONTEXT_PATH;

@Slf4j
@RequestMapping(TriggerController.TRIGGER_CONTROLLER_BASE_URL)
@SecurityRequirement(name = QUARTZ_MANAGER_SEC_OAS_SCHEMA)
@RestController
public class TriggerController {

   protected static final String TRIGGER_CONTROLLER_BASE_URL = QUARTZ_MANAGER_BASE_CONTEXT_PATH + "/triggers";

   private final TriggerService triggerService;

  public TriggerController(TriggerService triggerService) {
    this.triggerService = triggerService;
  }

  @GetMapping
  @Operation(summary = "Get a list of triggers")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Got the trigger list",
      content = { @Content(mediaType = "application/json",
        schema = @Schema(implementation = TriggerKeyDTO.class)) })
  })
  public List<TriggerKeyDTO> listTriggers() throws SchedulerException {
    return triggerService.fetchTriggers();
  }

  @GetMapping("/{group}/{name}")
  @Operation(summary = "Get trigger details")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Got trigger details",
      content = { @Content(mediaType = "application/json",
        schema = @Schema(implementation = TriggerDTO.class)) }),
    @ApiResponse(responseCode = "404", description = "Trigger not found", content = @Content)
  })
  public TriggerDTO getTrigger(@PathVariable String group, @PathVariable String name) throws SchedulerException, TriggerNotFoundException {
    return triggerService.getTrigger(group, name);
  }

  @PostMapping("/{group}/{name}")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Schedule a new trigger")
  public TriggerDTO postTrigger(@PathVariable String group, @PathVariable String name, @Valid @RequestBody TriggerInputDTO triggerInputDTO) throws SchedulerException, ClassNotFoundException, ParseException {
    log.info("TRIGGER - CREATING a trigger {} {}", name, triggerInputDTO);
    TriggerDTO newTriggerDTO = triggerService.scheduleTrigger(group, name, triggerInputDTO);
    log.info("TRIGGER - CREATED a trigger {}", newTriggerDTO);
    return newTriggerDTO;
  }

  @PutMapping("/{group}/{name}")
  @Operation(summary = "Reschedule a trigger")
  public TriggerDTO rescheduleTrigger(@PathVariable String group, @PathVariable String name, @Valid @RequestBody TriggerInputDTO triggerInputDTO) throws SchedulerException, TriggerNotFoundException, ParseException {
    log.info("TRIGGER - RESCHEDULING the trigger {} {}", name, triggerInputDTO);
    TriggerDTO triggerDTO = triggerService.rescheduleTrigger(group, name, triggerInputDTO);
    log.info("TRIGGER - RESCHEDULED the trigger {}", triggerDTO);
    return triggerDTO;
  }

  @PostMapping("/{group}/{name}/pause")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Pause a trigger")
  public void pauseTrigger(@PathVariable String group, @PathVariable String name) throws SchedulerException, TriggerNotFoundException {
    triggerService.pauseTrigger(group, name);
  }

  @PostMapping("/{group}/{name}/resume")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Resume a trigger")
  public void resumeTrigger(@PathVariable String group, @PathVariable String name) throws SchedulerException, TriggerNotFoundException {
    triggerService.resumeTrigger(group, name);
  }

  @DeleteMapping("/{group}/{name}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Unschedule a trigger")
  public void unscheduleTrigger(@PathVariable String group, @PathVariable String name) throws SchedulerException, TriggerNotFoundException {
    triggerService.unscheduleTrigger(group, name);
  }

}
