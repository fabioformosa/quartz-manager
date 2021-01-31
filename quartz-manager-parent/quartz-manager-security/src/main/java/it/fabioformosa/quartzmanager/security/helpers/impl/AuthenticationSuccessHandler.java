package it.fabioformosa.quartzmanager.security.helpers.impl;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

//@Component
//@ConditionalOnProperty(prefix = "quartz-manager.security.login-model", name = "form-login-enabled", havingValue = "true", matchIfMissing = true)
public class AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private final JwtAuthenticationSuccessHandler jwtAuthenticationSuccessHandler;

  //  @Autowired
  public AuthenticationSuccessHandler(JwtAuthenticationSuccessHandler jwtAuthenticationSuccessHandler) {
    super();
    this.jwtAuthenticationSuccessHandler = jwtAuthenticationSuccessHandler;
  }

  public String cookieMustBeDeletedAtLogout() {
    return jwtAuthenticationSuccessHandler.cookieMustBeDeletedAtLogout();
  }

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication ) throws IOException, ServletException {
    clearAuthenticationAttributes(request);
    jwtAuthenticationSuccessHandler.onLoginSuccess(authentication, response);
  }

}
