package it.fabioformosa.quartzmanager.controllers;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import lombok.Generated;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Hidden
@RestController
@RequestMapping
@Generated
public class HealthCheckController {

  @ResponseStatus(code = HttpStatus.OK)
  @GetMapping("/")
  @Operation(description = "Health Check")
  public String healthCheck() {
    log.trace("Health check called");
    return "OK";
  }


}
