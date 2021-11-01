package it.fabioformosa.quartzmanager.controllers;

import io.swagger.annotations.Api;
import it.fabioformosa.quartzmanager.dto.SchedulerConfigParam;
import it.fabioformosa.quartzmanager.dto.TriggerDTO;
import it.fabioformosa.quartzmanager.services.SchedulerService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping(TriggerController.TRIGGER_CONTROLLER_BASE_URL)
@RestController
@Api(value = "triggers")
public class TriggerController {

  static public final String TRIGGER_CONTROLLER_BASE_URL = "/quartz-manager/triggers";

  @Value("${quartz-manager.jobClass}")
  private String jobClassname;

  private SchedulerService schedulerService;

  public TriggerController(SchedulerService schedulerService) {
    this.schedulerService = schedulerService;
  }

  @GetMapping("/{name}")
  public TriggerDTO getTrigger(@PathVariable String name) throws SchedulerException {
    return schedulerService.getTriggerByName(name);
  }

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/{name}")
  public TriggerDTO postTrigger(@PathVariable String name, @RequestBody SchedulerConfigParam config) throws SchedulerException, ClassNotFoundException {
    log.info("TRIGGER - CREATING a trigger {} {}", name, config);
    TriggerDTO newTriggerDTO = schedulerService.scheduleNewTrigger(name, jobClassname, config);
    log.info("TRIGGER - CREATED a trigger {}", newTriggerDTO);
    return newTriggerDTO;
  }

  @PutMapping("/{name}")
  public TriggerDTO rescheduleTrigger(@PathVariable String name, @RequestBody SchedulerConfigParam config) throws SchedulerException {
    log.info("TRIGGER - RESCHEDULING the trigger {} {}", name, config);
    TriggerDTO triggerDTO = schedulerService.rescheduleTrigger(name, config);
    log.info("TRIGGER - RESCHEDULED the trigger {}", triggerDTO);
    return triggerDTO;
  }


}
