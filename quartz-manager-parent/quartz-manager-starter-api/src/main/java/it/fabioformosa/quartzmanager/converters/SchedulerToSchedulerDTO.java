package it.fabioformosa.quartzmanager.converters;

import it.fabioformosa.metamorphosis.core.converters.AbstractBaseConverterToDTO;
import it.fabioformosa.quartzmanager.dto.SchedulerDTO;
import lombok.SneakyThrows;
import org.quartz.Scheduler;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.stereotype.Component;

@Component
public class SchedulerToSchedulerDTO extends AbstractBaseConverterToDTO<Scheduler, SchedulerDTO> {

  @SneakyThrows
  @Override
  protected void convert(Scheduler source, SchedulerDTO target) {
    target.setName(source.getSchedulerName());
    target.setInstanceId(source.getSchedulerInstanceId());
    target.setTriggerKeys(source.getTriggerKeys(GroupMatcher.anyTriggerGroup()));
  }

}
