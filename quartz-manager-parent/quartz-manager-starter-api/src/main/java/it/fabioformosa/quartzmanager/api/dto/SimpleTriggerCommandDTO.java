package it.fabioformosa.quartzmanager.api.dto;

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
