package it.fabioformosa.quartzmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SchedulerConfigParam {
  @NotNull
  public Long triggerPerDay;
  @NotNull
  public Integer maxCount;
  public int timesTriggered;
}
