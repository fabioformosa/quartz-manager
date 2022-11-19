package it.fabioformosa.quartzmanager.api.converters;

import it.fabioformosa.metamorphosis.core.converters.AbstractBaseConverterToDTO;
import it.fabioformosa.quartzmanager.api.dto.TriggerKeyDTO;
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
