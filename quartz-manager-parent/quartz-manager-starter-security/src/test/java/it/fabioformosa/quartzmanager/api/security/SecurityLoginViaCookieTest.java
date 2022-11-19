package it.fabioformosa.quartzmanager.api.security;

import it.fabioformosa.quartzmanager.api.security.models.UserTokenState;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
  "quartz-manager.security.login-model.form-login-enabled = true",
  "quartz-manager.security.login-model.userpwd-filter-enabled = false",
  "quartz-manager.security.jwt.enabled=true",
  "quartz-manager.security.jwt.secret=bibidibobidiboo",
  "quartz-manager.security.jwt.expiration-in-sec=28800",
  "quartz-manager.security.jwt.header-strategy.enabled=false",
  "quartz-manager.security.jwt.header-strategy.header=Authorization",
  "quartz-manager.security.jwt.cookie-strategy.enabled=true",
  "quartz-manager.security.jwt.cookie-strategy.cookie=AUTH-TOKEN",
  "quartz-manager.security.accounts.in-memory.enabled=true",
  "quartz-manager.security.accounts.in-memory.users[0].username=foo",
  "quartz-manager.security.accounts.in-memory.users[0].password=bar",
  "quartz-manager.security.accounts.in-memory.users[0].roles[0]=admin",
})
class SecurityLoginViaCookieTest extends AbstractSecurityLoginTest {

  @Test
  void givenAnAnonymousUser_whenTheLoginIsSubmitted_thenShouldReturn2xx() {
    ResponseEntity<UserTokenState> responseEntity = doLogin();
    Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(responseEntity.getBody().getAccessToken()).isNotEmpty();
    Assertions.assertThat(responseEntity.getBody().getExpiresInSec()).isNotNull().isPositive();
    Assertions.assertThat(responseEntity.getHeaders().get("set-cookie")).hasSizeGreaterThan(0);
    Assertions.assertThat(responseEntity.getHeaders().get("set-cookie").get(0)).startsWith("AUTH-TOKEN");
  }

}
