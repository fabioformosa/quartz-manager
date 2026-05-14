package it.fabioformosa.quartzmanager.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.fabioformosa.quartzmanager.api.common.config.OpenAPIConfigConsts;
import it.fabioformosa.quartzmanager.api.common.config.QuartzManagerPaths;
import it.fabioformosa.quartzmanager.api.dto.ScheduledJobDTO;
import it.fabioformosa.quartzmanager.api.dto.ScheduledJobInputDTO;
import it.fabioformosa.quartzmanager.api.exceptions.JobNotFoundException;
import it.fabioformosa.quartzmanager.api.services.JobService;
import org.quartz.SchedulerException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

@RequestMapping(QuartzManagerPaths.QUARTZ_MANAGER_BASE_CONTEXT_PATH)
@SecurityRequirement(name = OpenAPIConfigConsts.QUARTZ_MANAGER_SEC_OAS_SCHEMA)
@RestController
public class JobController {
  public static final String JOB_CONTROLLER_BASE_URL = QuartzManagerPaths.QUARTZ_MANAGER_BASE_CONTEXT_PATH + "/jobs";
  public static final String JOB_CLASSES_CONTROLLER_BASE_URL = QuartzManagerPaths.QUARTZ_MANAGER_BASE_CONTEXT_PATH + "/job-classes";
  private final JobService jobService;

  public JobController(JobService jobService) {
    this.jobService = jobService;
  }

  @GetMapping("/job-classes")
  @Operation(summary = "Get the list of job classes eligible for Quartz-Manager")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Return a list of qualified java classes",
      content = {@Content(mediaType = "application/json",
        schema = @Schema(implementation = String.class))})
  })
  public List<String> listJobs() {
    return jobService.getJobClasses().stream().map(Class::getName).collect(Collectors.toList());
  }

  @GetMapping("/jobs")
  @Operation(summary = "Get the list of scheduled jobs")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Return a list of scheduled jobs",
      content = {@Content(mediaType = "application/json",
        schema = @Schema(implementation = ScheduledJobDTO.class))})
  })
  public List<ScheduledJobDTO> listScheduledJobs() throws SchedulerException {
    return jobService.fetchScheduledJobs();
  }

  @GetMapping("/jobs/{group}/{name}")
  @Operation(summary = "Get a scheduled job")
  public ScheduledJobDTO getScheduledJob(@PathVariable String group, @PathVariable String name) throws SchedulerException, JobNotFoundException {
    return jobService.getScheduledJob(group, name);
  }

  @PostMapping("/jobs/{group}/{name}")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Create a stored job")
  public ScheduledJobDTO createJob(@PathVariable String group, @PathVariable String name, @Valid @RequestBody ScheduledJobInputDTO scheduledJobInputDTO) throws SchedulerException, ClassNotFoundException {
    return jobService.createJob(group, name, scheduledJobInputDTO);
  }

  @PutMapping("/jobs/{group}/{name}")
  @Operation(summary = "Update a stored job")
  public ScheduledJobDTO updateJob(@PathVariable String group, @PathVariable String name, @Valid @RequestBody ScheduledJobInputDTO scheduledJobInputDTO) throws SchedulerException, ClassNotFoundException, JobNotFoundException {
    return jobService.updateJob(group, name, scheduledJobInputDTO);
  }

  @PostMapping("/jobs/{group}/{name}/trigger")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Trigger a job now")
  public void triggerJob(@PathVariable String group, @PathVariable String name) throws SchedulerException, JobNotFoundException {
    jobService.triggerJob(group, name);
  }

  @DeleteMapping("/jobs/{group}/{name}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Delete a job")
  public void deleteJob(@PathVariable String group, @PathVariable String name) throws SchedulerException, JobNotFoundException {
    jobService.deleteJob(group, name);
  }

}
