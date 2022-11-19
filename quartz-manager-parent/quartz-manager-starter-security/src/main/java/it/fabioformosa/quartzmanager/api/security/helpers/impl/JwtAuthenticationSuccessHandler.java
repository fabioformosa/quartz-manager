package it.fabioformosa.quartzmanager.api.security.helpers.impl;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;

public interface JwtAuthenticationSuccessHandler {

  String cookieMustBeDeletedAtLogout();

  void onLoginSuccess(Authentication authentication, HttpServletResponse response) throws IOException;
}
