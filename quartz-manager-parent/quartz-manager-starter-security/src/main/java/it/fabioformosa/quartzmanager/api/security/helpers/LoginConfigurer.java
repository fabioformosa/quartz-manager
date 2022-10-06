package it.fabioformosa.quartzmanager.api.security.helpers;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * It configures filters to authenticate credentials sent by client or to set authenticationSuccessHandler
 *
 * Implement this interface for a login strategy
 *
 */
public interface LoginConfigurer {

  /**
   * If the authentication is based on cookie, it returns the name of cookie to be erased at the logout
   */
  String cookieMustBeDeletedAtLogout();

  HttpSecurity login(String loginPath, HttpSecurity http, AuthenticationManager authenticationManager) throws Exception;

}
