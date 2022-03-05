package it.fabioformosa.quartzmanager.services;

import it.fabioformosa.quartzmanager.exceptions.TriggerNotFoundException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.springframework.core.convert.ConversionService;

public class AbstractSchedulerService {

  protected Scheduler scheduler;
  protected ConversionService conversionService;

  public AbstractSchedulerService(Scheduler scheduler, ConversionService conversionService) {
    this.scheduler = scheduler;
    this.conversionService = conversionService;
  }

  protected Trigger getTriggerByName(String name) throws SchedulerException, TriggerNotFoundException {
    Trigger trigger = scheduler.getTrigger(new TriggerKey(name));
    if(trigger == null)
      throw new TriggerNotFoundException(name);
    return trigger;
  }
}
