package it.fabioformosa.quartzmanager.api.security.helpers.impl;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private final JwtAuthenticationSuccessHandler jwtAuthenticationSuccessHandler;

  public AuthenticationSuccessHandler(JwtAuthenticationSuccessHandler jwtAuthenticationSuccessHandler) {
    super();
    this.jwtAuthenticationSuccessHandler = jwtAuthenticationSuccessHandler;
  }

  public String cookieMustBeDeletedAtLogout() {
    return jwtAuthenticationSuccessHandler.cookieMustBeDeletedAtLogout();
  }

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                      Authentication authentication) throws IOException {
    clearAuthenticationAttributes(request);
    jwtAuthenticationSuccessHandler.onLoginSuccess(authentication, response);
  }

}
