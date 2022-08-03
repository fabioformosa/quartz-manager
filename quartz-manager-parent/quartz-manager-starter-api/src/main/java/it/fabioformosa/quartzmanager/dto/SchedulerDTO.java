package it.fabioformosa.quartzmanager.dto;

import it.fabioformosa.quartzmanager.enums.SchedulerStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.quartz.TriggerKey;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SchedulerDTO {
  private String name;
  private String instanceId;
  private SchedulerStatus status;
  private Set<TriggerKey> triggerKeys;
}
