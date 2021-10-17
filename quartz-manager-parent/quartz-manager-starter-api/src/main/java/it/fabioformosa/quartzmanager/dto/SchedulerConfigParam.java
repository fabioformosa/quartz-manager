package it.fabioformosa.quartzmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SchedulerConfigParam {
  public long triggerPerDay;
  public int maxCount;
  public int timesTriggered;
}
