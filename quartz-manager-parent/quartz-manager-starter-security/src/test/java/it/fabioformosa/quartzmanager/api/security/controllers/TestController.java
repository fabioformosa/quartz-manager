package it.fabioformosa.quartzmanager.api.security.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping
@RestController
public class TestController {

  public static final String QUARTZ_MANAGER = "/quartz-manager";

  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/dmz")
  public void getDMZTest(){

  }

  @ResponseStatus(HttpStatus.OK)
  @GetMapping(QUARTZ_MANAGER + "/scheduler")
  public void getQuartzManagerScheduler(){

  }

}
