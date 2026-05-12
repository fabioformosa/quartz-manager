package it.fabioformosa.quartzmanager.api.services;

import it.fabioformosa.quartzmanager.api.dto.SimpleTriggerCommandDTO;
import it.fabioformosa.quartzmanager.api.dto.SimpleTriggerDTO;
import it.fabioformosa.quartzmanager.api.exceptions.ResourceConflictException;
import it.fabioformosa.quartzmanager.api.exceptions.TriggerNotFoundException;
import it.fabioformosa.quartzmanager.api.exceptions.UnsupportedTriggerTypeException;
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
    return getSimpleTrigger("DEFAULT", name);
  }

  public SimpleTriggerDTO getSimpleTrigger(String group, String name) throws SchedulerException, TriggerNotFoundException {
    Trigger trigger = scheduler.getTrigger(TriggerKey.triggerKey(name, group));
    if (trigger == null)
      throw new TriggerNotFoundException(group, name);
    if (!(trigger instanceof SimpleTrigger simpleTrigger))
      throw new UnsupportedTriggerTypeException(group, name);
    SimpleTriggerDTO simpleTriggerDTO = conversionService.convert(simpleTrigger, SimpleTriggerDTO.class);
    simpleTriggerDTO.setState(scheduler.getTriggerState(simpleTrigger.getKey()).name());
    return simpleTriggerDTO;
  }

  public SimpleTriggerDTO scheduleSimpleTrigger(SimpleTriggerCommandDTO simpleTriggerCommandDTO) throws SchedulerException, ClassNotFoundException {
    TriggerKey triggerKey = TriggerKey.triggerKey(simpleTriggerCommandDTO.getTriggerName(), simpleTriggerCommandDTO.getTriggerGroup());
    if (scheduler.checkExists(triggerKey))
      throw new ResourceConflictException("Trigger " + triggerKey + " already exists");

    SimpleTrigger newSimpleTrigger = conversionService.convert(simpleTriggerCommandDTO, SimpleTrigger.class);
    if (simpleTriggerCommandDTO.getSimpleTriggerInputDTO().getJobKey() != null) {
      JobKey jobKey = JobKey.jobKey(
        simpleTriggerCommandDTO.getSimpleTriggerInputDTO().getJobKey().getName(),
        simpleTriggerCommandDTO.getSimpleTriggerInputDTO().getJobKey().getGroup()
      );
      if (!scheduler.checkExists(jobKey))
        throw new ResourceConflictException("Job " + jobKey + " does not exist");
      newSimpleTrigger = newSimpleTrigger.getTriggerBuilder().forJob(jobKey).build();
      scheduler.scheduleJob(newSimpleTrigger);
    }
    else {
      Class<? extends Job> jobClass = Class.forName(simpleTriggerCommandDTO.getSimpleTriggerInputDTO().getJobClass()).asSubclass(Job.class);
      JobDetail jobDetail = JobBuilder.newJob()
        .ofType(jobClass)
        .storeDurably(false)
        .build();
      scheduler.scheduleJob(jobDetail, newSimpleTrigger);
    }

    return conversionService.convert(newSimpleTrigger, SimpleTriggerDTO.class);
  }

  public SimpleTriggerDTO rescheduleSimpleTrigger(SimpleTriggerCommandDTO triggerCommandDTO) throws SchedulerException, TriggerNotFoundException {
    TriggerKey triggerKey = TriggerKey.triggerKey(triggerCommandDTO.getTriggerName(), triggerCommandDTO.getTriggerGroup());
    Trigger existingTrigger = scheduler.getTrigger(triggerKey);
    if (existingTrigger == null)
      throw new TriggerNotFoundException(triggerCommandDTO.getTriggerGroup(), triggerCommandDTO.getTriggerName());

    SimpleTrigger newSimpleTrigger = conversionService.convert(triggerCommandDTO, SimpleTrigger.class);
    newSimpleTrigger = newSimpleTrigger.getTriggerBuilder()
      .forJob(existingTrigger.getJobKey())
      .build();

    scheduler.rescheduleJob(triggerKey, newSimpleTrigger);

    return conversionService.convert(newSimpleTrigger, SimpleTriggerDTO.class);
  }

}
