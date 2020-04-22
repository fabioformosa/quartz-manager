package it.fabioformosa.quartzmanager.security.auth;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.SneakyThrows;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import it.fabioformosa.quartzmanager.security.JwtTokenHelper;

import java.io.IOException;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  private final JwtTokenHelper jwtTokenHelper;
  private JwtAuthenticationSuccessHandler jwtAuthenticationSuccessHandler;

  public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtTokenHelper jwtTokenHelper, JwtAuthenticationSuccessHandler jwtAuthenticationSuccessHandler) {
    this.jwtTokenHelper = jwtTokenHelper;
    this.jwtAuthenticationSuccessHandler = jwtAuthenticationSuccessHandler;
    setAuthenticationManager(authenticationManager);
  }

  @SneakyThrows
  @Override
  protected void successfulAuthentication(HttpServletRequest req,
      HttpServletResponse res,
      FilterChain chain,
      Authentication auth) {
    UserDetails user = (UserDetails) auth.getPrincipal();
    String token = jwtTokenHelper.generateToken(user.getUsername());
    jwtTokenHelper.setHeader(res, token);

    jwtAuthenticationSuccessHandler.onSuccess(auth, res);
  }
}
