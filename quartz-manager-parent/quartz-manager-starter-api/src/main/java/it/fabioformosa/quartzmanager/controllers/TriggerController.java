package it.fabioformosa.quartzmanager.controllers;

import io.swagger.annotations.Api;
import it.fabioformosa.quartzmanager.dto.SchedulerConfigParam;
import it.fabioformosa.quartzmanager.dto.TriggerDTO;
import it.fabioformosa.quartzmanager.services.SchedulerService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/quartz-manager/triggers")
@RestController
@Api(value = "triggers")
public class TriggerController {

  @Value("${quartz-manager.jobClass}")
  private String jobClassname;

  private Scheduler scheduler;
  private SchedulerService schedulerService;
  private ConversionService conversionService;

  public TriggerController(Scheduler scheduler, SchedulerService schedulerService, ConversionService conversionService) {
    this.scheduler = scheduler;
    this.schedulerService = schedulerService;
    this.conversionService = conversionService;
  }

  @GetMapping("/{name}")
  public TriggerDTO getTrigger(@PathVariable String name) throws SchedulerException {
    Trigger trigger = scheduler.getTrigger(new TriggerKey(name));
    TriggerDTO triggerDTO = conversionService.convert(trigger, TriggerDTO.class);
    return triggerDTO;
  }

  @PostMapping("/{name}")
  public TriggerDTO postTrigger(@PathVariable String name, @RequestBody SchedulerConfigParam config) throws SchedulerException, ClassNotFoundException {
    log.info("TRIGGER - POST trigger {}", config);
    int intervalInMills = SchedulerService.fromTriggerPerDayToMillsInterval(config.getTriggerPerDay());

    Class<? extends Job> jobClass = (Class<? extends Job>) Class.forName(jobClassname);
    JobDetail jobDetail = JobBuilder.newJob()
      .ofType(jobClass)
      .storeDurably(false)
      .build();

    Trigger newTrigger = TriggerBuilder.newTrigger()
      .withSchedule(
        SimpleScheduleBuilder.simpleSchedule()
          .withIntervalInMilliseconds(intervalInMills)
          .withRepeatCount(config.getMaxCount() - 1)
          .withMisfireHandlingInstructionNextWithRemainingCount()
      )
      .build();

//    Optional<TriggerKey> optionalTriggerKey = schedulerService.getTriggerByKey(name);
//    TriggerKey triggerKey = optionalTriggerKey.orElse(TriggerKey.triggerKey(name));

    scheduler.scheduleJob(jobDetail, newTrigger);
//    scheduler.rescheduleJob(triggerKey, newTrigger);

    TriggerDTO newTriggerDTO = conversionService.convert(newTrigger, TriggerDTO.class);

    log.info("Rescheduled new trigger {}", newTriggerDTO);
    return newTriggerDTO;
  }


}
