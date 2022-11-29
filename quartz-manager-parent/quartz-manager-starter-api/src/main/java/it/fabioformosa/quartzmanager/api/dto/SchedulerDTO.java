package it.fabioformosa.quartzmanager.api.dto;

import it.fabioformosa.quartzmanager.api.enums.SchedulerStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.quartz.TriggerKey;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SchedulerDTO {
  private String name;
  private String instanceId;
  private SchedulerStatus status;
  private Set<TriggerKey> triggerKeys;
}
