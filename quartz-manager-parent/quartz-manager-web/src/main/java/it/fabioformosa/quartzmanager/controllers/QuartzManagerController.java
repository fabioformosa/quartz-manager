package it.fabioformosa.quartzmanager.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping
@Api(value = "Healthy Check")
public class QuartzManagerController {

  @ResponseStatus(code = HttpStatus.OK)
  @GetMapping("/")
  public void healthyCheck() {
    log.debug("Healthy check called");
  }


}
