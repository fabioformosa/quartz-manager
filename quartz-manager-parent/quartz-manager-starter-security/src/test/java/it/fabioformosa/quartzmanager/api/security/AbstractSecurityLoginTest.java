package it.fabioformosa.quartzmanager.api.security;

import it.fabioformosa.quartzmanager.api.security.models.UserTokenState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static it.fabioformosa.quartzmanager.api.common.config.QuartzManagerPaths.QUARTZ_MANAGER_LOGIN_PATH;

public abstract class AbstractSecurityLoginTest {
  @Autowired
  private TestRestTemplate testRestTemplate;

  protected ResponseEntity<UserTokenState> doLogin() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("username", "foo");
    map.add("password", "bar");

    HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

    ResponseEntity<UserTokenState> responseEntity = testRestTemplate.exchange(QUARTZ_MANAGER_LOGIN_PATH, HttpMethod.POST, entity, UserTokenState.class);
    return responseEntity;
  }
}
