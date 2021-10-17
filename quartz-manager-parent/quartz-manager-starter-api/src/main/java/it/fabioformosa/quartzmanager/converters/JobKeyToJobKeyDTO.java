package it.fabioformosa.quartzmanager.converters;

import it.fabioformosa.metamorphosis.core.converters.AbstractBaseConverterToDTO;
import it.fabioformosa.quartzmanager.dto.JobKeyDTO;
import org.quartz.JobKey;
import org.springframework.stereotype.Component;

@Component
public class JobKeyToJobKeyDTO extends AbstractBaseConverterToDTO<JobKey, JobKeyDTO> {
  @Override
  protected void convert(JobKey source, JobKeyDTO target) {
    target.setName(source.getName());
    target.setGroup(source.getGroup());
  }
}
