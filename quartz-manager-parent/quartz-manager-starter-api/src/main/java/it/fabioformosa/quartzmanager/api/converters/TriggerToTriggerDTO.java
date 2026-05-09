package it.fabioformosa.quartzmanager.api.converters;

import it.fabioformosa.metamorphosis.core.converters.AbstractBaseConverter;
import it.fabioformosa.quartzmanager.api.dto.JobDetailDTO;
import it.fabioformosa.quartzmanager.api.dto.JobKeyDTO;
import it.fabioformosa.quartzmanager.api.dto.TriggerDTO;
import it.fabioformosa.quartzmanager.api.dto.TriggerKeyDTO;
import org.quartz.JobKey;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class TriggerToTriggerDTO<S extends Trigger, T extends TriggerDTO> extends AbstractBaseConverter<S, T> {

  @Autowired
  private TriggerKeyToTriggerKeyDTO triggerKeyToTriggerKeyDTO;

  @Autowired
  private JobKeyToJobKeyDTO jobKeyToJobKeyDTO;

  @Autowired
  private JobKeyToJobDetailDTO jobKeyToJobDetailDTO;

  @Override
  protected void convert(S source, T target) {
    TriggerKey triggerKey = source.getKey();
    TriggerKeyDTO triggerKeyDTO = triggerKeyToTriggerKeyDTO.convert(triggerKey);
    target.setTriggerKeyDTO(triggerKeyDTO);

    target.setStartTime(source.getStartTime());
    target.setDescription(source.getDescription());
    target.setEndTime(source.getEndTime());
    target.setFinalFireTime(source.getFinalFireTime());
    target.setMisfireInstruction(source.getMisfireInstruction());
    target.setNextFireTime(source.getNextFireTime());
    target.setPriority(source.getPriority());
    target.setMayFireAgain(source.mayFireAgain());

    JobKey jobKey = source.getJobKey();
    if (jobKey == null) {
      return;
    }

    JobKeyDTO jobKeyDTO = jobKeyToJobKeyDTO.convert(jobKey);
    target.setJobKeyDTO(jobKeyDTO);

    JobDetailDTO jobDetailDTO = jobKeyToJobDetailDTO.convert(jobKey);
    target.setJobDetailDTO(jobDetailDTO);
  }

}
