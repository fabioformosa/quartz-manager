package it.fabioformosa.quartzmanager.api.controllers;

import it.fabioformosa.quartzmanager.api.QuartManagerApplicationTests;
import it.fabioformosa.quartzmanager.api.services.TriggerService;
import org.junit.jupiter.api.AfterEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@ContextConfiguration(classes = {QuartManagerApplicationTests.class})
@WebMvcTest(controllers = TriggerController.class, properties = {
  "quartz-manager.jobClassPackages=it.fabioformosa.quartzmanager.jobs"
})
class TriggerControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private TriggerService triggerService;

  @AfterEach
  void cleanUp(){
    Mockito.reset(triggerService);
  }

}
