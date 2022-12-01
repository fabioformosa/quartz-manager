package it.fabioformosa.quartzmanager.api.services;

import it.fabioformosa.quartzmanager.api.dto.SimpleTriggerCommandDTO;
import it.fabioformosa.quartzmanager.api.dto.SimpleTriggerDTO;
import it.fabioformosa.quartzmanager.api.exceptions.TriggerNotFoundException;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

@Service
public class SimpleTriggerService extends AbstractSchedulerService {

  public SimpleTriggerService(@Qualifier("quartzManagerScheduler") Scheduler scheduler, ConversionService conversionService) {
    super(scheduler, conversionService);
  }

  public SimpleTriggerDTO getSimpleTriggerByName(String name) throws SchedulerException, TriggerNotFoundException {
    Trigger trigger = getTriggerByName(name);
    return conversionService.convert(trigger, SimpleTriggerDTO.class);
  }

  public SimpleTriggerDTO scheduleSimpleTrigger(SimpleTriggerCommandDTO simpleTriggerCommandDTO) throws SchedulerException, ClassNotFoundException {
    Class<? extends Job> jobClass = (Class<? extends Job>) Class.forName(simpleTriggerCommandDTO.getSimpleTriggerInputDTO().getJobClass());
    JobDetail jobDetail = JobBuilder.newJob()
      .ofType(jobClass)
      .storeDurably(false)
      .build();

    SimpleTrigger newSimpleTrigger = conversionService.convert(simpleTriggerCommandDTO, SimpleTrigger.class);
    scheduler.scheduleJob(jobDetail, newSimpleTrigger);

    return conversionService.convert(newSimpleTrigger, SimpleTriggerDTO.class);
  }

  public SimpleTriggerDTO rescheduleSimpleTrigger(SimpleTriggerCommandDTO triggerCommandDTO) throws SchedulerException {
    SimpleTrigger newSimpleTrigger = conversionService.convert(triggerCommandDTO, SimpleTrigger.class);

    TriggerKey triggerKey = TriggerKey.triggerKey(triggerCommandDTO.getTriggerName());
    scheduler.rescheduleJob(triggerKey, newSimpleTrigger);

    return conversionService.convert(newSimpleTrigger, SimpleTriggerDTO.class);
  }

}
