package it.fabioformosa.quartzmanager.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.fabioformosa.quartzmanager.api.dto.CurrentExecutionDTO;
import it.fabioformosa.quartzmanager.api.services.ExecutionService;
import org.quartz.SchedulerException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static it.fabioformosa.quartzmanager.api.common.config.OpenAPIConfigConsts.QUARTZ_MANAGER_SEC_OAS_SCHEMA;
import static it.fabioformosa.quartzmanager.api.common.config.QuartzManagerPaths.QUARTZ_MANAGER_BASE_CONTEXT_PATH;

@RequestMapping(ExecutionController.EXECUTION_CONTROLLER_BASE_URL)
@SecurityRequirement(name = QUARTZ_MANAGER_SEC_OAS_SCHEMA)
@RestController
public class ExecutionController {

  protected static final String EXECUTION_CONTROLLER_BASE_URL = QUARTZ_MANAGER_BASE_CONTEXT_PATH + "/executions";

  private final ExecutionService executionService;

  public ExecutionController(ExecutionService executionService) {
    this.executionService = executionService;
  }

  @GetMapping("/current")
  @Operation(summary = "Get currently executing jobs")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Return currently executing jobs",
      content = { @Content(mediaType = "application/json",
        schema = @Schema(implementation = CurrentExecutionDTO.class)) })
  })
  public List<CurrentExecutionDTO> getCurrentExecutions() throws SchedulerException {
    return executionService.getCurrentExecutions();
  }

  @GetMapping("/recovering")
  @Operation(summary = "Get currently recovering job executions")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Return currently recovering job executions",
      content = { @Content(mediaType = "application/json",
        schema = @Schema(implementation = CurrentExecutionDTO.class)) })
  })
  public List<CurrentExecutionDTO> getRecoveringExecutions() throws SchedulerException {
    return executionService.getRecoveringExecutions();
  }
}
