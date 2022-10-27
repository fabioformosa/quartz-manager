package it.fabioformosa.quartzmanager.api.dto;

import it.fabioformosa.quartzmanager.api.validators.ValidRepetition;
import lombok.*;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
@ValidRepetition
public class SimpleTriggerInputDTO extends TriggerCommandDTO implements RepetitionDTO {

  private Integer repeatCount;

  private Long repeatInterval;
}
