package it.fabioformosa.quartzmanager.services;

import it.fabioformosa.quartzmanager.dto.TriggerDTO;
import it.fabioformosa.quartzmanager.dto.TriggerKeyDTO;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class TriggerService {

  private Scheduler scheduler;
  private ConversionService conversionService;

  public TriggerService(Scheduler scheduler, ConversionService conversionService) {
    this.scheduler = scheduler;
    this.conversionService = conversionService;
  }

  public List<TriggerDTO> fetchTriggers() throws SchedulerException {
    Set<TriggerKey> triggerKeys = scheduler.getTriggerKeys(GroupMatcher.anyTriggerGroup());
    return (List<TriggerDTO>) conversionService.convert(triggerKeys,
      TypeDescriptor.collection(Set.class, TypeDescriptor.valueOf(TriggerKey.class)),
      TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(TriggerKeyDTO.class)));
  }

}