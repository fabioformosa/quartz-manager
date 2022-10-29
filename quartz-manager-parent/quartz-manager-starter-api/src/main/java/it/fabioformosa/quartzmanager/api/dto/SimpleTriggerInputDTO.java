package it.fabioformosa.quartzmanager.api.dto;

import it.fabioformosa.quartzmanager.api.validators.ValidTriggerRepetition;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Positive;

@ValidTriggerRepetition
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class SimpleTriggerInputDTO extends TriggerCommandDTO implements TriggerRepetitionDTO {
  private Integer repeatCount;

  @Positive
  private Long repeatInterval;
}
