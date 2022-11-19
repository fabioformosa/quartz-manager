package it.fabioformosa.quartzmanager.api.services;

import it.fabioformosa.quartzmanager.api.dto.SchedulerDTO;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

@Service
public class SchedulerService extends AbstractSchedulerService{

  public SchedulerService(@Qualifier("quartzManagerScheduler") Scheduler scheduler, ConversionService conversionService) {
    super(scheduler, conversionService);
  }

  public SchedulerDTO getScheduler() {
    return conversionService.convert(scheduler, SchedulerDTO.class);
  }

  public void standby() throws SchedulerException {
    scheduler.standby();
  }
  public void start() throws SchedulerException {
    scheduler.start();
  }
  public void shutdown() throws SchedulerException {
    scheduler.shutdown(true);
  }

}
