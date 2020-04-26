package it.fabioformosa.quartzmanager.configuration.helpers.impl;

import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.web.filter.GenericFilterBean;

import it.fabioformosa.quartzmanager.configuration.helpers.LoginConfigurer;
import it.fabioformosa.quartzmanager.security.auth.JwtAuthenticationFilter;
import it.fabioformosa.quartzmanager.security.auth.JwtAuthenticationSuccessHandler;

//@Component
//@ConditionalOnProperty(prefix = "quartz-manager.security.login-model", name = "userpwd-filter-enabled", havingValue = "true", matchIfMissing = false)
public class UsernamePasswordFiterLoginConfig implements LoginConfigurer {

  //  @Autowired
  private final JwtAuthenticationSuccessHandler jwtAuthenticationSuccessHandler;

  public UsernamePasswordFiterLoginConfig(JwtAuthenticationSuccessHandler jwtAuthenticationSuccessHandler) {
    super();
    this.jwtAuthenticationSuccessHandler = jwtAuthenticationSuccessHandler;
  }

  public GenericFilterBean authenticationProcessingFilter(String loginPath, AuthenticationManager authenticationManager) throws Exception {
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
    return http.addFilterAfter(authenticationProcessingFilter(loginPath, authenticationManager), AbstractPreAuthenticatedProcessingFilter.class);
  }

}
