package it.fabioformosa.quartzmanager.configuration.helpers.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

import it.fabioformosa.quartzmanager.configuration.helpers.LoginConfig;
import it.fabioformosa.quartzmanager.security.auth.AuthenticationFailureHandler;
import it.fabioformosa.quartzmanager.security.auth.AuthenticationSuccessHandler;

@Component
@ConditionalOnProperty(prefix = "quartz-manager.security.login-model", name = "form-login-enabled", havingValue = "true", matchIfMissing = true)
public class FormLoginConfig implements LoginConfig {

  private static final String API_LOGIN = "/api/login";

  @Autowired
  private AuthenticationSuccessHandler authenticationSuccessHandler;

  @Autowired
  private AuthenticationFailureHandler authenticationFailureHandler;

  @Override
  public HttpSecurity login(
      HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
    return http.formLogin().loginPage(API_LOGIN).successHandler(authenticationSuccessHandler).failureHandler(authenticationFailureHandler).and();
  }

}
