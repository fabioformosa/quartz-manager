package it.fabioformosa.quartzmanager.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class JobDetailDTO {
  private String jobClassName;
  private String description;

}
