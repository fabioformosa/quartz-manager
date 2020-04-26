package it.fabioformosa.quartzmanager.security.auth;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;

public interface JwtAuthenticationSuccessHandler {
  void onSuccess(Authentication authentication, HttpServletResponse response) throws IOException;

  String cookieMustBeDeletedAtLogout();
}
