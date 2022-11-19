package it.fabioformosa.quartzmanager.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.fabioformosa.quartzmanager.api.common.config.OpenAPIConfigConsts;
import it.fabioformosa.quartzmanager.api.common.config.QuartzManagerPaths;
import it.fabioformosa.quartzmanager.api.dto.SimpleTriggerCommandDTO;
import it.fabioformosa.quartzmanager.api.dto.SimpleTriggerDTO;
import it.fabioformosa.quartzmanager.api.dto.SimpleTriggerInputDTO;
import it.fabioformosa.quartzmanager.api.dto.TriggerDTO;
import it.fabioformosa.quartzmanager.api.services.SimpleTriggerService;
import it.fabioformosa.quartzmanager.api.exceptions.TriggerNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RequestMapping(SimpleTriggerController.SIMPLE_TRIGGER_CONTROLLER_BASE_URL)
@SecurityRequirement(name = OpenAPIConfigConsts.QUARTZ_MANAGER_SEC_OAS_SCHEMA)
@RestController
public class SimpleTriggerController {

  protected static final String SIMPLE_TRIGGER_CONTROLLER_BASE_URL = QuartzManagerPaths.QUARTZ_MANAGER_BASE_CONTEXT_PATH + "/simple-triggers";

  private final SimpleTriggerService simpleSchedulerService;

  public SimpleTriggerController(SimpleTriggerService simpleSchedulerService) {
    this.simpleSchedulerService = simpleSchedulerService;
  }

  @GetMapping("/{name}")
  @Operation(summary = "Get a simple trigger by name")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Got the trigger by its name",
      content = { @Content(mediaType = "application/json",
        schema = @Schema(implementation = SimpleTriggerDTO.class)) }),
    @ApiResponse(responseCode = "404", description = "Trigger not found",
      content = @Content)
  })
  public SimpleTriggerDTO getSimpleTrigger(@PathVariable String name) throws SchedulerException, TriggerNotFoundException {
    return simpleSchedulerService.getSimpleTriggerByName(name);
  }

  @PostMapping("/{name}")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Schedule a new simple trigger")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "201", description = "Scheduled a new simple trigger",
      content = { @Content(mediaType = "application/json",
        schema = @Schema(implementation = SimpleTriggerDTO.class)) }),
    @ApiResponse(responseCode = "400", description = "Invalid trigger configuration",
      content = @Content)
  })
  public SimpleTriggerDTO postSimpleTrigger(@PathVariable String name, @Valid @RequestBody SimpleTriggerInputDTO simpleTriggerInputDTO) throws SchedulerException, ClassNotFoundException {
    log.info("SIMPLE TRIGGER - CREATING a SimpleTrigger {} {}", name, simpleTriggerInputDTO);
    SimpleTriggerCommandDTO simpleTriggerCommandDTO = SimpleTriggerCommandDTO.builder()
      .triggerName(name)
      .simpleTriggerInputDTO(simpleTriggerInputDTO)
      .build();
    SimpleTriggerDTO newTriggerDTO = simpleSchedulerService.scheduleSimpleTrigger(simpleTriggerCommandDTO);
    log.info("SIMPLE TRIGGER - CREATED a SimpleTrigger {}", newTriggerDTO);
    return newTriggerDTO;
  }

  @PutMapping("/{name}")
  @Operation(summary = "Reschedule a simple trigger")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Rescheduled a simple trigger",
      content = { @Content(mediaType = "application/json",
        schema = @Schema(implementation = TriggerDTO.class)) }),
    @ApiResponse(responseCode = "400", description = "Invalid trigger configuration",
      content = @Content)
  })
  public TriggerDTO rescheduleSimpleTrigger(@PathVariable String name, @Valid @RequestBody SimpleTriggerInputDTO simpleTriggerInputDTO) throws SchedulerException {
    log.info("SIMPLE TRIGGER - RESCHEDULING the trigger {} {}", name, simpleTriggerInputDTO);
    SimpleTriggerCommandDTO simpleTriggerCommandDTO = SimpleTriggerCommandDTO.builder()
      .triggerName(name)
      .simpleTriggerInputDTO(simpleTriggerInputDTO)
      .build();
    TriggerDTO triggerDTO = simpleSchedulerService.rescheduleSimpleTrigger(simpleTriggerCommandDTO);
    log.info("SIMPLE TRIGGER - RESCHEDULED the trigger {}", triggerDTO);
    return triggerDTO;
  }


}
