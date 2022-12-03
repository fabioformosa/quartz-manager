package it.fabioformosa.quartzmanager.api.controllers;

import it.fabioformosa.quartzmanager.api.exceptions.ResourceConflictException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(TestController.TEST_CONTROLLER_BASE_URL)
@RestController
public class TestController {

  public static final String TEST_CONTROLLER_BASE_URL = "/test-controller";

  @PostMapping("/test-conflict")
  public void raiseConflictException(){
    throw new ResourceConflictException(1000L, "another entity has found with the same ID");
  }

}
