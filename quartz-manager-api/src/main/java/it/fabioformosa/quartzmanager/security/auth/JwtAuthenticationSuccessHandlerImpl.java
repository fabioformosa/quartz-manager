package it.fabioformosa.quartzmanager.security.auth;

import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.fabioformosa.quartzmanager.configuration.properties.JwtSecurityProperties;
import it.fabioformosa.quartzmanager.security.JwtTokenHelper;
import it.fabioformosa.quartzmanager.security.model.UserTokenState;

//@Component
public class JwtAuthenticationSuccessHandlerImpl implements JwtAuthenticationSuccessHandler {

  //  @Autowired
  private final JwtSecurityProperties jwtSecurityProps;

  private final JwtTokenHelper jwtTokenHelper;

  private final ObjectMapper objectMapper;

  //  @Value("${server.servlet.context-path}")
  private final String contextPath;

  @Autowired
  public JwtAuthenticationSuccessHandlerImpl(String contextPath, JwtSecurityProperties jwtSecurityProps, JwtTokenHelper jwtTokenHelper, ObjectMapper objectMapper) {
    this.contextPath = contextPath;
    this.jwtSecurityProps = jwtSecurityProps;
    this.jwtTokenHelper = jwtTokenHelper;
    this.objectMapper = objectMapper;
  }

  @Override
  public String cookieMustBeDeletedAtLogout() {
    if(!jwtSecurityProps.getCookieStrategy().isEnabled())
      return null;
    return jwtSecurityProps.getCookieStrategy().getCookie();
  }

  @Override
  public void onSuccess(Authentication authentication, HttpServletResponse response) throws IOException {
    User user = (User) authentication.getPrincipal();

    String jwtToken = jwtTokenHelper.generateToken(user.getUsername());

    if(jwtSecurityProps.getCookieStrategy().isEnabled()) {
      Cookie authCookie = new Cookie(jwtSecurityProps.getCookieStrategy().getCookie(), jwtToken);
      authCookie.setHttpOnly(true);
      authCookie.setMaxAge((int) jwtSecurityProps.getExpirationInSec());
      authCookie.setPath(contextPath);
      response.addCookie(authCookie);
    }

    if(jwtSecurityProps.getHeaderStrategy().isEnabled())
      jwtTokenHelper.setHeader(response, jwtToken);

    UserTokenState userTokenState = new UserTokenState(jwtToken, jwtSecurityProps.getExpirationInSec());
    String jwtResponse = objectMapper.writeValueAsString(userTokenState);
    response.setContentType("application/json");
    response.getWriter().write(jwtResponse);
  }
}
