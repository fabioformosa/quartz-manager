package it.fabioformosa.quartzmanager.controllers;

import it.fabioformosa.quartzmanager.QuartManagerApplicationTests;
import it.fabioformosa.quartzmanager.controllers.utils.InvalidSchedulerConfigParamProvider;
import it.fabioformosa.quartzmanager.controllers.utils.TestUtils;
import it.fabioformosa.quartzmanager.controllers.utils.TriggerUtils;
import it.fabioformosa.quartzmanager.dto.SchedulerConfigParam;
import it.fabioformosa.quartzmanager.dto.TriggerDTO;
import it.fabioformosa.quartzmanager.services.LegacySchedulerService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ContextConfiguration(classes = {QuartManagerApplicationTests.class})
@WebMvcTest(controllers = TriggerController.class, properties = {
  "quartz-manager.jobClassPackages=it.fabioformosa.quartzmanager.jobs"
})
class TriggerControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private LegacySchedulerService schedulerService;

  @AfterEach
  void cleanUp(){
    Mockito.reset(schedulerService);
  }

  @ParameterizedTest
  @ArgumentsSource(InvalidSchedulerConfigParamProvider.class)
  void givenAnInvalidSchedulerConfigParam_whenRequestedANewTrigger_thenAnErrorIsReturned(SchedulerConfigParam invalidSchedulerConfigParam) throws Exception {
    mockMvc.perform(post(TriggerController.TRIGGER_CONTROLLER_BASE_URL + "/mytrigger")
      .contentType(MediaType.APPLICATION_JSON)
      .content(TestUtils.toJson(invalidSchedulerConfigParam)))
      .andExpect(MockMvcResultMatchers.status().is4xxClientError());
  }

  @Test
  void whenGetIsCalled_thenATriggerIsReturned() throws Exception {
    TriggerDTO expectedTriggerDTO = TriggerUtils.getTriggerInstance("mytrigger");
    Mockito.when(schedulerService.getLegacyTriggerByName("mytrigger")).thenReturn(expectedTriggerDTO);

    mockMvc.perform(get(TriggerController.TRIGGER_CONTROLLER_BASE_URL + "/mytrigger")
      .contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk())
      .andExpect(MockMvcResultMatchers.content().json(TestUtils.toJson(expectedTriggerDTO)));
  }

  @Test
  void givenATriggerName_whenPutSchedulerConfigParam_thenTheTriggerIsRescheduled() throws Exception {
    SchedulerConfigParam expectedConfigParam = buildSimpleSchedulerConfigParam();
    TriggerDTO expectedTriggerDTO = TriggerUtils.getTriggerInstance("mytrigger");
    Mockito.when(schedulerService.rescheduleTrigger("mytrigger", buildSimpleSchedulerConfigParam())).thenReturn(expectedTriggerDTO);

    mockMvc.perform(put(TriggerController.TRIGGER_CONTROLLER_BASE_URL + "/mytrigger")
      .contentType(MediaType.APPLICATION_JSON)
      .content(TestUtils.toJson(expectedConfigParam)))
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andExpect(MockMvcResultMatchers.content().json(TestUtils.toJson(expectedTriggerDTO)));
  }

  @ParameterizedTest
  @ArgumentsSource(InvalidSchedulerConfigParamProvider.class)
  void givenAnInvalidSchedulerConfigParam_whenATriggerIsRescheduled_thenAnErrorIsReturned(SchedulerConfigParam invalidSchedulerConfigParam) throws Exception {
    mockMvc.perform(put(TriggerController.TRIGGER_CONTROLLER_BASE_URL + "/mytrigger")
        .contentType(MediaType.APPLICATION_JSON)
        .content(TestUtils.toJson(invalidSchedulerConfigParam)))
      .andExpect(MockMvcResultMatchers.status().is4xxClientError());
  }

  private SchedulerConfigParam buildSimpleSchedulerConfigParam() {
    return SchedulerConfigParam.builder().maxCount(20).triggerPerDay(20000L).build();
  }

}
