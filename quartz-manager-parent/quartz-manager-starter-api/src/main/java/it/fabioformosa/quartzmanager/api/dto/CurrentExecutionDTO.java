package it.fabioformosa.quartzmanager.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CurrentExecutionDTO {
  private String fireInstanceId;
  private JobKeyDTO jobKeyDTO;
  private TriggerKeyDTO triggerKeyDTO;
  private Date fireTime;
  private Date scheduledFireTime;
  private Date previousFireTime;
  private Date nextFireTime;
  private long runTime;
  private int refireCount;
  private boolean recovering;
  private String node;
}
