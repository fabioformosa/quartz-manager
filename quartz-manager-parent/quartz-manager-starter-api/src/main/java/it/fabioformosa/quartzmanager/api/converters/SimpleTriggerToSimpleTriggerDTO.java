package it.fabioformosa.quartzmanager.api.converters;

import it.fabioformosa.quartzmanager.api.dto.SimpleTriggerDTO;
import org.quartz.SimpleTrigger;
import org.springframework.stereotype.Component;

@Component
public class SimpleTriggerToSimpleTriggerDTO extends TriggerToTriggerDTO<SimpleTrigger, SimpleTriggerDTO> {

  @Override
  protected void convert(SimpleTrigger source, SimpleTriggerDTO target) {
    super.convert(source, target);
    target.setTimesTriggered(source.getTimesTriggered());
    target.setRepeatCount(source.getRepeatCount());
    target.setRepeatInterval(source.getRepeatInterval());
    target.setMisfireInstruction(source.getMisfireInstruction());
    target.setJobDataMap(source.getJobDataMap());
  }

  @Override
  protected SimpleTriggerDTO createOrRetrieveTarget(SimpleTrigger source) {
    return new SimpleTriggerDTO();
  }
}
