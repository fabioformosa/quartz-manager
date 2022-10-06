package it.fabioformosa.quartzmanager.api.security.helpers.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

public class LogoutSuccess implements LogoutSuccessHandler {

  private final ObjectMapper objectMapper;

  public LogoutSuccess(ObjectMapper objectMapper) {
    super();
    this.objectMapper = objectMapper;
  }

  @Override
  public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse response, Authentication authentication)
      throws IOException, ServletException {
    Map<String, String> result = new HashMap<>();
    result.put( "result", "success" );
    response.setContentType("application/json");
    response.getWriter().write(objectMapper.writeValueAsString(result));
    response.setStatus(HttpServletResponse.SC_OK);

  }

}
