package it.fabioformosa.quartzmanager.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@ToString
public class SimpleTriggerCommandDTO {
  private String triggerName;
  private SimpleTriggerInputDTO simpleTriggerInputDTO;
}
