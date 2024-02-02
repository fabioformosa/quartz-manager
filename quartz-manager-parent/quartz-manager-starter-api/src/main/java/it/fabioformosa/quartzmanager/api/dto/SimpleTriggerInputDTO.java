package it.fabioformosa.quartzmanager.api.dto;

import it.fabioformosa.quartzmanager.api.validators.ValidTriggerRepetition;
import lombok.*;
import lombok.experimental.SuperBuilder;
import javax.annotation.Nullable;
import javax.validation.constraints.Positive;
import java.util.Map;

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

  @Nullable
  private Map<String, ?> jobDataMap;
}
