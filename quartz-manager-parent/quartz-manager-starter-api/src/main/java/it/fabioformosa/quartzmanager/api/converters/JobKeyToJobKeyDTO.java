package it.fabioformosa.quartzmanager.api.converters;

import it.fabioformosa.metamorphosis.core.converters.AbstractBaseConverterToDTO;
import it.fabioformosa.quartzmanager.api.dto.JobKeyDTO;
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
