package it.fabioformosa.quartzmanager.security.auth;

import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface JwtAuthenticationSuccessHandler {
  void onSuccess(Authentication authentication, HttpServletResponse response) throws IOException;
}
