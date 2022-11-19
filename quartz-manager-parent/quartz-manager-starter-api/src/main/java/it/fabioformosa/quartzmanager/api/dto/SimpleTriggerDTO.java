package it.fabioformosa.quartzmanager.api.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor @AllArgsConstructor
@Data
@ToString(callSuper = true) @EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class SimpleTriggerDTO extends TriggerDTO{

  private int repeatCount;
  private long repeatInterval;
  private int timesTriggered;

}
