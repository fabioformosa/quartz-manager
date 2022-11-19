package it.fabioformosa.quartzmanager.api.services;

import it.fabioformosa.quartzmanager.api.dto.TriggerKeyDTO;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
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
    return (List<TriggerKeyDTO>) conversionService.convert(triggerKeys,
      TypeDescriptor.collection(Set.class, TypeDescriptor.valueOf(TriggerKey.class)),
      TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(TriggerKeyDTO.class)));
  }

}
