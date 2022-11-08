package it.fabioformosa.quartzmanager.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.fabioformosa.quartzmanager.api.dto.TriggerKeyDTO;
import it.fabioformosa.quartzmanager.api.services.TriggerService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
