package it.fabioformosa.quartzmanager.api.security.helpers.impl;

import it.fabioformosa.quartzmanager.api.security.helpers.LoginConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;

/**
 * It delegates the login to the @FormLoginConfigurer of the httpSecurity.
 *
 */
public class FormLoginConfig implements LoginConfigurer {

  private static final Logger log = LoggerFactory.getLogger(FormLoginConfig.class);

  private final AuthenticationSuccessHandler authenticationSuccessHandler;

  private final AuthenticationFailureHandler authenticationFailureHandler;


  public FormLoginConfig() {
    super();
    authenticationSuccessHandler = null;
    authenticationFailureHandler = null;
  }

  public FormLoginConfig(AuthenticationFailureHandler authenticationFailureHandler) {
    super();
    authenticationSuccessHandler = null;
    this.authenticationFailureHandler = authenticationFailureHandler;
  }

  public FormLoginConfig(AuthenticationSuccessHandler authenticationSuccessHandler) {
    super();
    this.authenticationSuccessHandler = authenticationSuccessHandler;
    authenticationFailureHandler = null;
  }

  public FormLoginConfig(AuthenticationSuccessHandler authenticationSuccessHandler,
      AuthenticationFailureHandler authenticationFailureHandler) {
    super();
    this.authenticationSuccessHandler = authenticationSuccessHandler;
    this.authenticationFailureHandler = authenticationFailureHandler;
  }

  @Override
  public String cookieMustBeDeletedAtLogout() {
    return authenticationSuccessHandler.cookieMustBeDeletedAtLogout();
  }

  @Override
  public HttpSecurity login(String loginPath,
      HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
    log.debug("Configuring login through FormLoginConfigurer...");

    FormLoginConfigurer<HttpSecurity> login = http.formLogin().loginPage(loginPath);

    if(authenticationSuccessHandler != null) {
      log.debug("Setting an authenticationSuccessHandler");
      login = login.successHandler(authenticationSuccessHandler);
    }

    if(authenticationFailureHandler != null) {
      log.debug("Setting an authenticationFailureHandler");
      login = login.failureHandler(authenticationFailureHandler);
    }

    return login.and();
  }

}
