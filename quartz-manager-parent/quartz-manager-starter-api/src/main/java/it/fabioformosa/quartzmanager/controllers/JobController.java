package it.fabioformosa.quartzmanager.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.fabioformosa.quartzmanager.services.JobService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static it.fabioformosa.quartzmanager.common.config.OpenAPIConfigConsts.BASIC_AUTH_SEC_OAS_SCHEME;
import static it.fabioformosa.quartzmanager.common.config.QuartzManagerPaths.QUARTZ_MANAGER_BASE_CONTEXT_PATH;

@RequestMapping(QUARTZ_MANAGER_BASE_CONTEXT_PATH + "/jobs")
@SecurityRequirement(name = BASIC_AUTH_SEC_OAS_SCHEME)
@RestController
public class JobController {
  final private JobService jobService;

  public JobController(JobService jobService) {
    this.jobService = jobService;
  }

  @GetMapping
  @Operation(summary = "Get the list of job classes eligible for Quartz-Manager")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Return a list of qualified java classes",
      content = { @Content(mediaType = "application/json",
        schema = @Schema(implementation = String.class)) })
  })
  public List<String> listJobs(){
    return jobService.getJobClasses().stream().map(Class::getName).collect(Collectors.toList());
  }

}
