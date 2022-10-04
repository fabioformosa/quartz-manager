package it.fabioformosa.quartzmanager.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.fabioformosa.quartzmanager.dto.SimpleTriggerCommandDTO;
import it.fabioformosa.quartzmanager.dto.SimpleTriggerDTO;
import it.fabioformosa.quartzmanager.dto.SimpleTriggerInputDTO;
import it.fabioformosa.quartzmanager.dto.TriggerDTO;
import it.fabioformosa.quartzmanager.exceptions.TriggerNotFoundException;
import it.fabioformosa.quartzmanager.services.SimpleTriggerService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static it.fabioformosa.quartzmanager.common.config.OpenAPIConfigConsts.BASIC_AUTH_SEC_OAS_SCHEME;
import static it.fabioformosa.quartzmanager.common.config.QuartzManagerPaths.QUARTZ_MANAGER_BASE_CONTEXT_PATH;

@Slf4j
@RequestMapping(SimpleTriggerController.SIMPLE_TRIGGER_CONTROLLER_BASE_URL)
@SecurityRequirement(name = BASIC_AUTH_SEC_OAS_SCHEME)
@RestController
public class SimpleTriggerController {

  static protected final String SIMPLE_TRIGGER_CONTROLLER_BASE_URL = QUARTZ_MANAGER_BASE_CONTEXT_PATH + "/simple-triggers";

  final private SimpleTriggerService simpleSchedulerService;

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
  @Operation(summary = "Create a new simple trigger")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "201", description = "Created a new simple trigger",
      content = { @Content(mediaType = "application/json",
        schema = @Schema(implementation = TriggerDTO.class)) }),
    @ApiResponse(responseCode = "400", description = "Invalid config supplied",
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
    @ApiResponse(responseCode = "400", description = "Invalid config supplied",
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
