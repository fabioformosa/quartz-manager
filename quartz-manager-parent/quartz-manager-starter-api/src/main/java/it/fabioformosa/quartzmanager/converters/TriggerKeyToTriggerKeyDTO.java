package it.fabioformosa.quartzmanager.converters;

import it.fabioformosa.metamorphosis.core.converters.AbstractBaseConverterToDTO;
import it.fabioformosa.quartzmanager.dto.TriggerKeyDTO;
import org.quartz.TriggerKey;
import org.springframework.stereotype.Component;

@Component
public class TriggerKeyToTriggerKeyDTO extends AbstractBaseConverterToDTO<TriggerKey, TriggerKeyDTO> {

  @Override
  protected void convert(TriggerKey source, TriggerKeyDTO target) {
    target.setName(source.getName());
    target.setGroup(source.getGroup());
  }
}
