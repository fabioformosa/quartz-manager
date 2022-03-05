package it.fabioformosa.quartzmanager.converters;

import it.fabioformosa.quartzmanager.dto.SimpleTriggerDTO;
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
  }

  @Override
  protected SimpleTriggerDTO createOrRetrieveTarget(SimpleTrigger source) {
    return new SimpleTriggerDTO();
  }
}
