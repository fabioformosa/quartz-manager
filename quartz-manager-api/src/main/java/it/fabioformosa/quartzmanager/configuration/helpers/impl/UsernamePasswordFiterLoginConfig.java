package it.fabioformosa.quartzmanager.configuration.helpers.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import it.fabioformosa.quartzmanager.configuration.helpers.LoginConfig;
import it.fabioformosa.quartzmanager.security.JwtTokenHelper;
import it.fabioformosa.quartzmanager.security.auth.JwtAuthenticationFilter;

@Component
@ConditionalOnProperty(prefix = "quartz-manager.security.login-model", name = "userpwd-filter-enabled", havingValue = "true", matchIfMissing = false)
public class UsernamePasswordFiterLoginConfig implements LoginConfig {

  private static final String API_LOGIN = "/api/login";

  @Autowired
  private JwtTokenHelper jwtTokenHelper;

  public GenericFilterBean authenticationProcessingFilter(AuthenticationManager authenticationManager) throws Exception {
    JwtAuthenticationFilter authenticationProcessingFilter = new JwtAuthenticationFilter(authenticationManager, jwtTokenHelper);
    authenticationProcessingFilter.setRequiresAuthenticationRequestMatcher(new RegexRequestMatcher(API_LOGIN, HttpMethod.POST.name(), false));
    return authenticationProcessingFilter;
  }

  @Override
  public HttpSecurity configureLoginHandler(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
    return http.addFilterAfter(authenticationProcessingFilter(authenticationManager), AbstractPreAuthenticatedProcessingFilter.class);
  }

}
