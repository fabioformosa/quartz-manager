package it.fabioformosa.quartzmanager.configuration.helpers.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import it.fabioformosa.quartzmanager.configuration.helpers.LoginConfigurer;
import it.fabioformosa.quartzmanager.security.auth.AuthenticationFailureHandler;
import it.fabioformosa.quartzmanager.security.auth.AuthenticationSuccessHandler;

//@Component
//@ConditionalOnProperty(prefix = "quartz-manager.security.login-model", name = "form-login-enabled", havingValue = "true", matchIfMissing = true)
public class FormLoginConfig implements LoginConfigurer {

  private final AuthenticationSuccessHandler authenticationSuccessHandler;

  private final AuthenticationFailureHandler authenticationFailureHandler;

  //  @Autowired
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
    return http.formLogin().loginPage(loginPath).successHandler(authenticationSuccessHandler).failureHandler(authenticationFailureHandler).and();
  }

}
