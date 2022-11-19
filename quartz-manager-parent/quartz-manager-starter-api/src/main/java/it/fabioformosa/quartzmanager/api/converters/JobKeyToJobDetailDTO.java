package it.fabioformosa.quartzmanager.api.converters;

import it.fabioformosa.metamorphosis.core.converters.AbstractBaseConverterToDTO;
import it.fabioformosa.quartzmanager.api.dto.JobDetailDTO;
import lombok.SneakyThrows;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class JobKeyToJobDetailDTO extends AbstractBaseConverterToDTO<JobKey, JobDetailDTO> {

  @Qualifier("quartzManagerScheduler")
  @Autowired
  private Scheduler scheduler;

  @SneakyThrows
  @Override
  protected void convert(JobKey jobKey, JobDetailDTO jobDetailDTO) {
    JobDetail jobDetail = scheduler.getJobDetail(jobKey);
    jobDetailDTO.setJobClassName(jobDetail.getJobClass().getName());
    jobDetailDTO.setDescription(jobDetail.getDescription());
  }
}
