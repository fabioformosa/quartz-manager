package it.fabioformosa.quartzmanager.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("/quartz-manager/jobs")
@RestController
public class JobController extends AbstractTriggerController {

  @GetMapping
  public List<String> listJobs(){
    List jobClasses = new ArrayList();
    jobClasses.add(jobClassname);
    return jobClasses;
  }

}
