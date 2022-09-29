package it.fabioformosa.quartzmanager.controllers;

import it.fabioformosa.quartzmanager.services.JobService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static it.fabioformosa.quartzmanager.controllers.AbstractQuartzManagerController.QUARTZ_MANAGER_CONTEXT_PATH;

@RequestMapping(QUARTZ_MANAGER_CONTEXT_PATH + "/jobs")
@RestController
public class JobController extends AbstractQuartzManagerController {
  final private JobService jobService;

  public JobController(JobService jobService) {
    this.jobService = jobService;
  }

  @GetMapping
  public List<String> listJobs(){
    return jobService.getJobClasses().stream().map(Class::getName).collect(Collectors.toList());
  }

}
