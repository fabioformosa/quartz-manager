package it.fabioformosa.quartzmanager.security.auth;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "quartz-manager.security.login-model", name = "form-login-enabled", havingValue = "true", matchIfMissing = true)
public class AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  @Autowired
  private JwtAuthenticationSuccessHandler jwtAuthenticationSuccessHandler;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication ) throws IOException, ServletException {
    clearAuthenticationAttributes(request);
    jwtAuthenticationSuccessHandler.onSuccess(authentication, response);
  }

}
