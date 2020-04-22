package it.fabioformosa.quartzmanager.security.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.fabioformosa.quartzmanager.security.JwtTokenHelper;
import it.fabioformosa.quartzmanager.security.model.UserTokenState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationSuccessHandlerImpl implements JwtAuthenticationSuccessHandler {

  @Value("${quartz-manager.security.jwt.expiration-in-sec}")
  private int EXPIRES_IN_SEC;

  @Value("${quartz-manager.security.jwt.cookie}")
  private String TOKEN_COOKIE;

  private final JwtTokenHelper jwtTokenHelper;

  private final ObjectMapper objectMapper;

  @Autowired
  public JwtAuthenticationSuccessHandlerImpl(JwtTokenHelper tokenHelper, ObjectMapper objectMapper) {
    this.jwtTokenHelper = tokenHelper;
    this.objectMapper = objectMapper;
  }

  @Override
  public void onSuccess(Authentication authentication, HttpServletResponse response) throws IOException {
    User user = (User) authentication.getPrincipal();

    String jws = jwtTokenHelper.generateToken(user.getUsername());

    //set cookie or set header?
    Cookie authCookie = new Cookie(TOKEN_COOKIE, jws);
    authCookie.setHttpOnly(true);
    authCookie.setMaxAge(EXPIRES_IN_SEC);
    authCookie.setPath("/quartz-manager");
    response.addCookie(authCookie);

    // JWT is also in the response
    UserTokenState userTokenState = new UserTokenState(jws, EXPIRES_IN_SEC);
    String jwtResponse = objectMapper.writeValueAsString(userTokenState);
    response.setContentType("application/json");
    response.getWriter().write(jwtResponse);
  }
}
