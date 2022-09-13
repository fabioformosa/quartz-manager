package it.fabioformosa.quartzmanager.controllers;

import it.fabioformosa.quartzmanager.services.JobService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/quartz-manager/jobs")
@RestController
public class JobController extends AbstractTriggerController {

  private JobService jobService;

  public JobController(JobService jobService) {
    this.jobService = jobService;
  }

  @GetMapping
  public List<String> listJobs(){
    return jobService.getJobClasses().stream().map(Class::getName).collect(Collectors.toList());
  }

}
