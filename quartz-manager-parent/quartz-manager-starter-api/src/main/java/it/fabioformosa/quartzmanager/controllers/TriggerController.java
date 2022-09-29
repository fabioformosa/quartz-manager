package it.fabioformosa.quartzmanager.controllers;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.fabioformosa.quartzmanager.dto.TriggerDTO;
import it.fabioformosa.quartzmanager.services.TriggerService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequestMapping(TriggerController.TRIGGER_CONTROLLER_BASE_URL)
@SecurityRequirement(name = "basic-auth")
@RestController
public class TriggerController extends AbstractQuartzManagerController {

  static protected final String TRIGGER_CONTROLLER_BASE_URL = QUARTZ_MANAGER_CONTEXT_PATH + "/triggers";

  final private TriggerService triggerService;

  public TriggerController(TriggerService triggerService) {
    this.triggerService = triggerService;
  }

  @GetMapping
  public List<TriggerDTO> listTriggers() throws SchedulerException {
    return triggerService.fetchTriggers();
  }

}
