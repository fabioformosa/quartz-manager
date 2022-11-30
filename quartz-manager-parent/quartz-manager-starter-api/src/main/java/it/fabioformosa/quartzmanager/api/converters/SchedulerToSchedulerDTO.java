package it.fabioformosa.quartzmanager.api.converters;

import it.fabioformosa.metamorphosis.core.converters.AbstractBaseConverterToDTO;
import it.fabioformosa.quartzmanager.api.dto.SchedulerDTO;
import it.fabioformosa.quartzmanager.api.enums.SchedulerStatus;
import lombok.SneakyThrows;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.stereotype.Component;

@Component
public class SchedulerToSchedulerDTO extends AbstractBaseConverterToDTO<Scheduler, SchedulerDTO> {

  @SneakyThrows
  @Override
  protected void convert(Scheduler source, SchedulerDTO target) {
    target.setName(source.getSchedulerName());
    target.setInstanceId(source.getSchedulerInstanceId());
    if(!source.isShutdown())
      target.setTriggerKeys(source.getTriggerKeys(GroupMatcher.anyTriggerGroup()));
    target.setStatus(buildTheSchedulerStatus(source));
  }

  private SchedulerStatus buildTheSchedulerStatus(Scheduler scheduler) throws SchedulerException {
    if (scheduler.isShutdown() || !scheduler.isStarted())
      return SchedulerStatus.STOPPED;
    else if (scheduler.isStarted() && scheduler.isInStandbyMode())
      return SchedulerStatus.PAUSED;
    return SchedulerStatus.RUNNING;
  }

}
