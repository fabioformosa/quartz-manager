package it.fabioformosa.quartzmanager.api.dto;

import it.fabioformosa.quartzmanager.api.validators.ValidTriggerRepetition;
import it.fabioformosa.quartzmanager.api.validators.JobTargetDTO;
import it.fabioformosa.quartzmanager.api.validators.ValidJobTarget;
import lombok.*;
import lombok.experimental.SuperBuilder;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Positive;
import java.util.Map;

@ValidTriggerRepetition
@ValidJobTarget
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class SimpleTriggerInputDTO extends TriggerCommandDTO implements TriggerRepetitionDTO, JobTargetDTO {
  private Integer repeatCount;

  @Positive
  private Long repeatInterval;

  @Nullable
  private Map<String, ?> jobDataMap;

  @Nullable
  private JobKeyDTO jobKey;
}
