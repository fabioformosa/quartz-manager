package it.fabioformosa.quartzmanager.controllers;

import it.fabioformosa.quartzmanager.QuartManagerApplicationTests;
import it.fabioformosa.quartzmanager.controllers.utils.TestUtils;
import it.fabioformosa.quartzmanager.controllers.utils.TriggerUtils;
import it.fabioformosa.quartzmanager.dto.SchedulerConfigParam;
import it.fabioformosa.quartzmanager.dto.TriggerDTO;
import it.fabioformosa.quartzmanager.services.SchedulerService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ContextConfiguration(classes = {QuartManagerApplicationTests.class})
@WebMvcTest(controllers = TriggerController.class, properties = {
  "quartz-manager.jobClass=it.fabioformosa.quartzmanager.jobs.myjobs.SampleJob"
})
class TriggerControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private SchedulerService schedulerService;

  @AfterEach
  void cleanUp(){
    Mockito.reset(schedulerService);
  }

  @Test
  void givenASchedulerConfigParam_whenPosted_thenANewTriggerIsCreated() throws Exception {
    TriggerDTO expectedTriggerDTO = TriggerUtils.getTriggerInstance();
    Mockito.when(schedulerService.scheduleNewTrigger(any(), any(), any())).thenReturn(expectedTriggerDTO);

    SchedulerConfigParam configParamToPost = SchedulerConfigParam.builder().maxCount(20).triggerPerDay(20000L).build();
    mockMvc.perform(
      post(TriggerController.TRIGGER_CONTROLLER_BASE_URL + "/mytrigger")
        .contentType(MediaType.APPLICATION_JSON)
        .content(TestUtils.toJson(configParamToPost))
      )
      .andExpect(MockMvcResultMatchers.status().isCreated())
      .andExpect(MockMvcResultMatchers.content().json(TestUtils.toJson(expectedTriggerDTO)))
    ;
  }

  @Test
  void whenGetIsCalled_thenATriggerIsReturned() throws Exception {
    TriggerDTO expectedTriggerDTO = TriggerUtils.getTriggerInstance();
    Mockito.when(schedulerService.getTriggerByName("mytrigger")).thenReturn(expectedTriggerDTO);

    mockMvc.perform(get(TriggerController.TRIGGER_CONTROLLER_BASE_URL + "/mytrigger")
      .contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk())
      .andExpect(MockMvcResultMatchers.content().json(TestUtils.toJson(expectedTriggerDTO)));
  }

}
