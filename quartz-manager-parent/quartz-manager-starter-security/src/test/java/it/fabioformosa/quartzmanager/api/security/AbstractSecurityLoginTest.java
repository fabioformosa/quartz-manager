package it.fabioformosa.quartzmanager.api.security;

import it.fabioformosa.quartzmanager.api.security.models.UserTokenState;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import static it.fabioformosa.quartzmanager.api.common.config.QuartzManagerPaths.QUARTZ_MANAGER_LOGIN_PATH;

public abstract class AbstractSecurityLoginTest {

  @LocalServerPort
  private int port;

  protected ResponseEntity<UserTokenState> doLogin() {
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("username", "foo");
    map.add("password", "bar");

    return RestClient.create("http://localhost:" + port)
      .post()
      .uri(QUARTZ_MANAGER_LOGIN_PATH)
      .contentType(MediaType.APPLICATION_FORM_URLENCODED)
      .body(map)
      .retrieve()
      .toEntity(UserTokenState.class);
  }
}
