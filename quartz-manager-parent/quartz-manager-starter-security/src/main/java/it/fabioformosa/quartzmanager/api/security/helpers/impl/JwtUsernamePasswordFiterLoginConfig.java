package it.fabioformosa.quartzmanager.api.security.helpers.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.web.filter.GenericFilterBean;

import it.fabioformosa.quartzmanager.api.security.helpers.LoginConfigurer;

/**
 * It adds a new filter @JwtAuthenticationFilter after @AbstractPreAuthenticatedProcessingFilter that match login path
 *
 */
public class JwtUsernamePasswordFiterLoginConfig implements LoginConfigurer {

  private static final Logger log = LoggerFactory.getLogger(JwtUsernamePasswordFiterLoginConfig.class);

  private final JwtAuthenticationSuccessHandler jwtAuthenticationSuccessHandler;

  public JwtUsernamePasswordFiterLoginConfig(JwtAuthenticationSuccessHandler jwtAuthenticationSuccessHandler) {
    super();
    this.jwtAuthenticationSuccessHandler = jwtAuthenticationSuccessHandler;
  }

  public GenericFilterBean authenticationProcessingFilter(String loginPath, AuthenticationManager authenticationManager) {
    JwtAuthenticationFilter authenticationProcessingFilter = new JwtAuthenticationFilter(authenticationManager, jwtAuthenticationSuccessHandler);
    authenticationProcessingFilter.setRequiresAuthenticationRequestMatcher(new RegexRequestMatcher(loginPath, HttpMethod.POST.name(), false));
    return authenticationProcessingFilter;
  }

  @Override
  public String cookieMustBeDeletedAtLogout() {
    return jwtAuthenticationSuccessHandler.cookieMustBeDeletedAtLogout();
  }

  @Override
  public HttpSecurity login(String loginPath, HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
    log.debug("Configuring login via JwtAuthenticationFilter...");
    return http.addFilterAfter(authenticationProcessingFilter(loginPath, authenticationManager), AbstractPreAuthenticatedProcessingFilter.class);
  }

}
