package it.fabioformosa.quartzmanager.services;

import it.fabioformosa.quartzmanager.dto.SimpleTriggerCommandDTO;
import it.fabioformosa.quartzmanager.dto.SimpleTriggerDTO;
import it.fabioformosa.quartzmanager.dto.TriggerDTO;
import it.fabioformosa.quartzmanager.exceptions.TriggerNotFoundException;
import org.quartz.*;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

@Service
public class SimpleTriggerSchedulerService extends AbstractSchedulerService {

  public SimpleTriggerSchedulerService(Scheduler scheduler, ConversionService conversionService) {
    super(scheduler, conversionService);
  }

  public SimpleTriggerDTO getSimpleTriggerByName(String name) throws SchedulerException, TriggerNotFoundException {
    Trigger trigger = getTriggerByName(name);
    return conversionService.convert(trigger, SimpleTriggerDTO.class);
  }

  public SimpleTriggerDTO scheduleSimpleTrigger(SimpleTriggerCommandDTO triggerCommandDTO) throws SchedulerException, ClassNotFoundException {
    Class<? extends Job> jobClass = (Class<? extends Job>) Class.forName(triggerCommandDTO.getSimpleTriggerInputDTO().getJobClass());
    JobDetail jobDetail = JobBuilder.newJob()
      .ofType(jobClass)
      .storeDurably(false)
      .build();

    SimpleTrigger newSimpleTrigger = conversionService.convert(triggerCommandDTO, SimpleTrigger.class);
    scheduler.scheduleJob(jobDetail, newSimpleTrigger);

    return conversionService.convert(newSimpleTrigger, SimpleTriggerDTO.class);
  }

  public TriggerDTO rescheduleSimpleTrigger(SimpleTriggerCommandDTO triggerCommandDTO) throws SchedulerException {
    //Optional<TriggerKey> optionalTriggerKey = getTriggerByKey(name);
//    TriggerKey triggerKey = optionalTriggerKey.orElse(TriggerKey.triggerKey(name));

    SimpleTrigger newSimpleTrigger = conversionService.convert(triggerCommandDTO, SimpleTrigger.class);

    TriggerKey triggerKey = TriggerKey.triggerKey(triggerCommandDTO.getTriggerName());
    scheduler.rescheduleJob(triggerKey, newSimpleTrigger);

    return conversionService.convert(newSimpleTrigger, SimpleTriggerDTO.class);
  }

}
