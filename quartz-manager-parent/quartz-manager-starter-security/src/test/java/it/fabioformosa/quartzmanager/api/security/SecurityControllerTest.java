package it.fabioformosa.quartzmanager.api.security;

import it.fabioformosa.quartzmanager.api.common.config.QuartzManagerPaths;
import it.fabioformosa.quartzmanager.api.security.controllers.TestController;
import it.fabioformosa.quartzmanager.api.security.properties.JwtSecurityProperties;
import org.assertj.core.api.Assertions;
import org.hamcrest.core.IsNot;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
  "quartz-manager.security.jwt.secret=bibidibobidiboo",
  "quartz-manager.security.jwt.expiration-in-sec=36000",
  "quartz-manager.security.jwt.header-strategy.enabled=false",
  "quartz-manager.security.jwt.header-strategy.header=Authorization",
  "quartz-manager.security.jwt.cookie-strategy.enabled=true",
  "quartz-manager.security.jwt.cookie-strategy.cookie=AUTH-TOKEN",
  "quartz-manager.security.accounts.in-memory.enabled=true",
  "quartz-manager.security.accounts.in-memory.users[0].username=foo",
  "quartz-manager.security.accounts.in-memory.users[0].password=bar",
  "quartz-manager.security.accounts.in-memory.users[0].roles[0]=admin",
})
class SecurityControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private JwtSecurityProperties jwtSecurityProperties;

  @Test
  void givenAnAnonymousUser_whenCalledADMZController_thenShouldRaiseForbidden() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/dmz"))
      .andExpect(status().isOk());
  }

  @Test
  void givenAnAnonymousUser_whenCalledATestScheduler_thenShouldRaiseForbidden() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get(TestController.QUARTZ_MANAGER + "/scheduler"))
      .andExpect(status().isUnauthorized());
  }

  @ParameterizedTest
  @ValueSource(strings =  {"/swagger-ui.html", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**"})
   void givenAnAnonymousUser_whenRequestedAnEndpointInWhitelist_thenShouldnotReturnForbidden(String whitelistEndpoint) throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get(whitelistEndpoint))
      .andExpect(status().is(IsNot.not(403)));
  }

  @Test
  @WithMockUser("admin")
  void givenAnUser_whenCalledATestScheduler_thenShouldReturn2xx() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get(TestController.QUARTZ_MANAGER + "/scheduler"))
      .andExpect(status().isOk());
  }

  @Test
  void givenAnAnonymousUser_whenCalledTheLoginPath_thenShouldReturn2xx() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.post(QuartzManagerPaths.QUARTZ_MANAGER_LOGIN_PATH)
        .contentType("application/x-www-form-urlencoded")
        .accept("application/json")
        .param("username", "foo")
        .param("password", "bar"))
      .andExpect(status().isOk());
  }

  @Test
  void givenSecurityProps_whenTheBootstrapHasCompleted_thenJWTPropertiesShouldBeSetAccordingly() throws Exception {
    Assertions.assertThat(jwtSecurityProperties.getExpirationInSec()).isEqualTo(36000);
    Assertions.assertThat(jwtSecurityProperties.getSecret()).isEqualTo("bibidibobidiboo");
  }

}
