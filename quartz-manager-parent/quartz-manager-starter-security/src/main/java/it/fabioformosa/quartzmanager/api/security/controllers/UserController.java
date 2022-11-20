package it.fabioformosa.quartzmanager.api.security.controllers;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.fabioformosa.quartzmanager.api.common.config.OpenAPIConfigConsts;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static it.fabioformosa.quartzmanager.api.common.config.QuartzManagerPaths.QUARTZ_MANAGER_AUTH_PATH;

@RestController
@Hidden
@SecurityRequirement(name = OpenAPIConfigConsts.QUARTZ_MANAGER_SEC_OAS_SCHEMA)
@RequestMapping(value = QUARTZ_MANAGER_AUTH_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

  public static final String WHOAMI_URL = "/whoami";

  @GetMapping(WHOAMI_URL)
  public ResponseEntity<Object> getLoggedUser() {
    SecurityContext context = SecurityContextHolder.getContext();
    if (context != null && context.getAuthentication() != null)
      return new ResponseEntity<>(context.getAuthentication().getPrincipal(), HttpStatus.OK);
    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }

}
