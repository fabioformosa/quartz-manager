package it.fabioformosa.quartzmanager.services;

import it.fabioformosa.quartzmanager.dto.SchedulerDTO;
import org.quartz.Scheduler;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

@Service
public class SchedulerService extends AbstractSchedulerService{

  public SchedulerService(Scheduler scheduler, ConversionService conversionService) {
    super(scheduler, conversionService);
  }

  public SchedulerDTO getScheduler() {
    return conversionService.convert(scheduler, SchedulerDTO.class);
  }

}
