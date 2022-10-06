package it.fabioformosa.quartzmanager.api.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ExceptionResponse {
  private String errorCode;
  private String errorMessage;
}
