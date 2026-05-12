package it.fabioformosa.quartzmanager.api.controllers.advices;

import it.fabioformosa.quartzmanager.api.exceptions.ExceptionResponse;
import it.fabioformosa.quartzmanager.api.exceptions.JobNotFoundException;
import it.fabioformosa.quartzmanager.api.exceptions.ResourceConflictException;
import it.fabioformosa.quartzmanager.api.exceptions.TriggerNotFoundException;
import it.fabioformosa.quartzmanager.api.exceptions.UnsupportedTriggerTypeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ExceptionHandlingController {

  @ExceptionHandler(ResourceConflictException.class)
  public ResponseEntity<ExceptionResponse> resourceConflict(ResourceConflictException ex) {
    ExceptionResponse response = new ExceptionResponse();
    response.setErrorCode("Conflict");
    response.setErrorMessage(ex.getMessage());
    return new ResponseEntity<>(response, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(TriggerNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ResponseBody
  public ExceptionResponse triggerNotFound(TriggerNotFoundException ex){
    return ExceptionResponse.builder().errorCode(HttpStatus.NOT_FOUND.toString()).errorMessage(ex.getMessage()).build();
  }

  @ExceptionHandler(JobNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ResponseBody
  public ExceptionResponse jobNotFound(JobNotFoundException ex){
    return ExceptionResponse.builder().errorCode(HttpStatus.NOT_FOUND.toString()).errorMessage(ex.getMessage()).build();
  }

  @ExceptionHandler(UnsupportedTriggerTypeException.class)
  public ResponseEntity<ExceptionResponse> unsupportedTriggerType(UnsupportedTriggerTypeException ex) {
    ExceptionResponse response = ExceptionResponse.builder()
      .errorCode(HttpStatus.CONFLICT.toString())
      .errorMessage(ex.getMessage())
      .build();
    return new ResponseEntity<>(response, HttpStatus.CONFLICT);
  }

}
