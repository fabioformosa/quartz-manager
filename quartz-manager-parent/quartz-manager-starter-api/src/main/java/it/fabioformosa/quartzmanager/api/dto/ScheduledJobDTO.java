package it.fabioformosa.quartzmanager.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ScheduledJobDTO {
  private JobKeyDTO jobKeyDTO;
  private String jobClassName;
  private String description;
  private boolean durable;
  private boolean requestsRecovery;
  private Map<String, ?> jobDataMap;
  private List<TriggerKeyDTO> triggerKeys;
}
