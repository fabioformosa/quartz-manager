package it.fabioformosa.quartzmanager.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import it.fabioformosa.quartzmanager.api.validators.ValidTriggerPeriod;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@ValidTriggerPeriod
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Data
public class TriggerCommandDTO implements TriggerPeriodDTO {
  @NotBlank
  private String jobClass;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date startDate;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date endDate;

  @Builder.Default
  private MisfireInstruction misfireInstruction = MisfireInstruction.MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT;
}
