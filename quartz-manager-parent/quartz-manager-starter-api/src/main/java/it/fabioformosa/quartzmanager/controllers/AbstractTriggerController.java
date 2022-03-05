package it.fabioformosa.quartzmanager.controllers;

import org.springframework.beans.factory.annotation.Value;

public class AbstractTriggerController {

  @Value("${quartz-manager.jobClass}")
  protected String jobClassname;

}
