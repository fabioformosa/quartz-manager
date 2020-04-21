package it.fabioformosa.quartzmanager.security.auth;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import it.fabioformosa.quartzmanager.security.JwtTokenHelper;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  private final JwtTokenHelper jwtTokenHelper;

  public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtTokenHelper jwtTokenHelper) {
    this.jwtTokenHelper = jwtTokenHelper;
    setAuthenticationManager(authenticationManager);
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest req,
      HttpServletResponse res,
      FilterChain chain,
      Authentication auth) {
    UserDetails user = (UserDetails) auth.getPrincipal();
    String token = jwtTokenHelper.generateToken(user.getUsername());
    jwtTokenHelper.setHeader(res, token);
  }
}