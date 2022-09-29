package it.fabioformosa.quartzmanager.controllers;

import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import static it.fabioformosa.quartzmanager.controllers.AbstractQuartzManagerController.QUARTZ_MANAGER_CONTEXT_PATH;

@RestController
@RequestMapping(value = UserController.USER_CONTROLLER_BASE_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

  static protected final String USER_CONTROLLER_BASE_URL = QUARTZ_MANAGER_CONTEXT_PATH + "/api";

  @GetMapping("/whoami")
  public @ResponseBody Object user() {
    SecurityContext context = SecurityContextHolder.getContext();
    if (context != null && context.getAuthentication() != null)
      return context.getAuthentication().getPrincipal();
    return "\"NO_AUTH\"";
  }

}
