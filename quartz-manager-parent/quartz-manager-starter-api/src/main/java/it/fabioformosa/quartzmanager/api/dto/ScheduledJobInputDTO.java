package it.fabioformosa.quartzmanager.api.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ScheduledJobInputDTO {
  @NotBlank
  private String jobClass;

  private String description;

  @Builder.Default
  private boolean durable = true;

  private boolean requestsRecovery;

  @Nullable
  private Map<String, ?> jobDataMap;
}
