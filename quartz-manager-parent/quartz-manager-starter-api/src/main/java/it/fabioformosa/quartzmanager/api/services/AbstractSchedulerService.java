package it.fabioformosa.quartzmanager.api.services;

import it.fabioformosa.quartzmanager.api.exceptions.TriggerNotFoundException;
import org.quartz.*;
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

  protected JobDetail getJobDetailByKey(JobKey jobKey) throws SchedulerException {
    return scheduler.getJobDetail(jobKey);
  }

}
