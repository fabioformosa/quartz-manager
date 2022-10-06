package it.fabioformosa.quartzmanager.api.security.helpers.impl;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.SneakyThrows;

/**
 * It extends the @UsernamePasswordAuthenticationFilter and it overrides the successfulAuthentication method to put jwtToken in the response
 *
 */
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  private JwtAuthenticationSuccessHandler jwtAuthenticationSuccessHandler;

  public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtAuthenticationSuccessHandler jwtAuthenticationSuccessHandler) {
    this.jwtAuthenticationSuccessHandler = jwtAuthenticationSuccessHandler;
    setAuthenticationManager(authenticationManager);
  }

  @SneakyThrows
  @Override
  protected void successfulAuthentication(HttpServletRequest req,
      HttpServletResponse res,
      FilterChain chain,
      Authentication auth) {
    jwtAuthenticationSuccessHandler.onLoginSuccess(auth, res);
  }
}
