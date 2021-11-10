package it.fabioformosa.quartzmanager.controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping
public class QuartzManagerController {

  @ResponseStatus(code = HttpStatus.OK)
  @GetMapping("/")
  @Operation(description = "Healthy Check")
  public void healthyCheck() {
    log.debug("Healthy check called");
  }


}
