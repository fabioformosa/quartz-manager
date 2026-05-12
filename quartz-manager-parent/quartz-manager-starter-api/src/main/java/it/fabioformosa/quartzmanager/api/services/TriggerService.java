package it.fabioformosa.quartzmanager.api.services;

import it.fabioformosa.quartzmanager.api.dto.TriggerDTO;
import it.fabioformosa.quartzmanager.api.dto.TriggerKeyDTO;
import it.fabioformosa.quartzmanager.api.exceptions.TriggerNotFoundException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class TriggerService {

  private Scheduler scheduler;
  private ConversionService conversionService;

  public TriggerService(@Qualifier("quartzManagerScheduler") Scheduler scheduler, ConversionService conversionService) {
    this.scheduler = scheduler;
    this.conversionService = conversionService;
  }

  public List<TriggerKeyDTO> fetchTriggers() throws SchedulerException {
    Set<TriggerKey> triggerKeys = scheduler.getTriggerKeys(GroupMatcher.anyTriggerGroup());
    return triggerKeys.stream()
      .map(triggerKey -> conversionService.convert(triggerKey, TriggerKeyDTO.class))
      .toList();
  }

  public TriggerDTO getTrigger(String group, String name) throws SchedulerException, TriggerNotFoundException {
    TriggerKey triggerKey = TriggerKey.triggerKey(name, group);
    Trigger trigger = scheduler.getTrigger(triggerKey);
    if (trigger == null)
      throw new TriggerNotFoundException(group, name);
    TriggerDTO triggerDTO = conversionService.convert(trigger, TriggerDTO.class);
    triggerDTO.setState(scheduler.getTriggerState(triggerKey).name());
    return triggerDTO;
  }

  public void pauseTrigger(String group, String name) throws SchedulerException, TriggerNotFoundException {
    TriggerKey triggerKey = requireTrigger(group, name);
    scheduler.pauseTrigger(triggerKey);
  }

  public void resumeTrigger(String group, String name) throws SchedulerException, TriggerNotFoundException {
    TriggerKey triggerKey = requireTrigger(group, name);
    scheduler.resumeTrigger(triggerKey);
  }

  public void unscheduleTrigger(String group, String name) throws SchedulerException, TriggerNotFoundException {
    TriggerKey triggerKey = requireTrigger(group, name);
    scheduler.unscheduleJob(triggerKey);
  }

  private TriggerKey requireTrigger(String group, String name) throws SchedulerException, TriggerNotFoundException {
    TriggerKey triggerKey = TriggerKey.triggerKey(name, group);
    if (!scheduler.checkExists(triggerKey))
      throw new TriggerNotFoundException(group, name);
    return triggerKey;
  }

}
