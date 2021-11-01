package it.fabioformosa.quartzmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TriggerDTO {
  private TriggerKeyDTO triggerKeyDTO;
  private int priority;
  private Date startTime;
  private String description;
  private Date endTime;
  private Date finalFireTime;
  private int misfireInstruction;
  private Date nextFireTime;
  private JobKeyDTO jobKeyDTO;
  private boolean mayFireAgain;
}
