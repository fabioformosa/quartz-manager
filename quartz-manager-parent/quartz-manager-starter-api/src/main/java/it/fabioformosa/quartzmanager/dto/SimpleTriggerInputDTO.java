package it.fabioformosa.quartzmanager.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class SimpleTriggerInputDTO extends TriggerCommandDTO {

  @NotNull
  private Integer repeatCount;

  @NotNull
  Long repeatInterval;
}
