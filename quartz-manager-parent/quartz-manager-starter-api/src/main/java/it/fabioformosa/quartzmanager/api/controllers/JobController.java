package it.fabioformosa.quartzmanager.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.fabioformosa.quartzmanager.api.common.config.OpenAPIConfigConsts;
import it.fabioformosa.quartzmanager.api.common.config.QuartzManagerPaths;
import it.fabioformosa.quartzmanager.api.services.JobService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RequestMapping(JobController.JOB_CONTROLLER_BASE_URL)
@SecurityRequirement(name = OpenAPIConfigConsts.QUARTZ_MANAGER_SEC_OAS_SCHEMA)
@RestController
public class JobController {
  public static final String JOB_CONTROLLER_BASE_URL = QuartzManagerPaths.QUARTZ_MANAGER_BASE_CONTEXT_PATH + "/jobs";
  private final JobService jobService;

  public JobController(JobService jobService) {
    this.jobService = jobService;
  }

  @GetMapping
  @Operation(summary = "Get the list of job classes eligible for Quartz-Manager")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Return a list of qualified java classes",
      content = {@Content(mediaType = "application/json",
        schema = @Schema(implementation = String.class))})
  })
  public List<String> listJobs() {
    return jobService.getJobClasses().stream().map(Class::getName).collect(Collectors.toList());
  }

}
