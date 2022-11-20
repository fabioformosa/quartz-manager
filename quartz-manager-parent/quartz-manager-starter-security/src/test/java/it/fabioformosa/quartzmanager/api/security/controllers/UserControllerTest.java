package it.fabioformosa.quartzmanager.api.security.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static it.fabioformosa.quartzmanager.api.common.config.QuartzManagerPaths.QUARTZ_MANAGER_AUTH_PATH;
import static it.fabioformosa.quartzmanager.api.security.controllers.UserController.WHOAMI_URL;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
  "quartz-manager.security.accounts.in-memory.enabled=true",
  "quartz-manager.security.accounts.in-memory.users[0].username=admin",
  "quartz-manager.security.accounts.in-memory.users[0].password=admin",
  "quartz-manager.security.accounts.in-memory.users[0].roles[0]=admin",
})
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  @WithMockUser("admin")
  void givenAnUser_whenCalledTheWhoamiEndpoint_thenShouldReturn2xx() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get(QUARTZ_MANAGER_AUTH_PATH + WHOAMI_URL))
      .andExpect(status().isOk());
  }

  @Test
  void givenAnAnonymousUser_whenCalledTheWhoamiEndpoint_thenShouldReturnNotFound() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get(QUARTZ_MANAGER_AUTH_PATH + WHOAMI_URL))
      .andExpect(status().isUnauthorized());
  }
}
