package it.fabioformosa.quartzmanager.api.controllers;

import it.fabioformosa.quartzmanager.api.QuartManagerApplicationTests;
import it.fabioformosa.quartzmanager.api.controllers.utils.TestUtils;
import it.fabioformosa.quartzmanager.api.dto.TriggerDTO;
import it.fabioformosa.quartzmanager.api.dto.TriggerKeyDTO;
import it.fabioformosa.quartzmanager.api.services.TriggerService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ContextConfiguration(classes = {QuartManagerApplicationTests.class})
@WebMvcTest(controllers = TriggerController.class, properties = {
  "quartz-manager.jobClassPackages=it.fabioformosa.quartzmanager.jobs"
})
class TriggerControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private TriggerService triggerService;

  @AfterEach
  void cleanUp(){
    Mockito.reset(triggerService);
  }

  @Test
  void whenListTriggersIsCalled_thenTriggersAreReturned() throws Exception {
    List<TriggerKeyDTO> triggerKeys = List.of(TriggerKeyDTO.builder().name("sampleTrigger").group("DEFAULT").build());
    Mockito.when(triggerService.fetchTriggers()).thenReturn(triggerKeys);

    mockMvc.perform(get(TriggerController.TRIGGER_CONTROLLER_BASE_URL).contentType(MediaType.APPLICATION_JSON))
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andExpect(MockMvcResultMatchers.content().json(TestUtils.toJson(triggerKeys)));
  }

  @Test
  void whenGetTriggerIsCalled_thenTriggerIsReturned() throws Exception {
    TriggerDTO triggerDTO = TriggerDTO.builder()
      .triggerKeyDTO(TriggerKeyDTO.builder().name("sampleTrigger").group("DEFAULT").build())
      .state("NORMAL")
      .type("SimpleTrigger")
      .build();
    Mockito.when(triggerService.getTrigger("DEFAULT", "sampleTrigger")).thenReturn(triggerDTO);

    mockMvc.perform(get(TriggerController.TRIGGER_CONTROLLER_BASE_URL + "/DEFAULT/sampleTrigger").contentType(MediaType.APPLICATION_JSON))
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andExpect(MockMvcResultMatchers.content().json(TestUtils.toJson(triggerDTO)));
  }

  @Test
  void whenPauseTriggerIsCalled_thenNoContentIsReturned() throws Exception {
    mockMvc.perform(post(TriggerController.TRIGGER_CONTROLLER_BASE_URL + "/DEFAULT/sampleTrigger/pause").contentType(MediaType.APPLICATION_JSON))
      .andExpect(MockMvcResultMatchers.status().isNoContent());

    Mockito.verify(triggerService).pauseTrigger("DEFAULT", "sampleTrigger");
  }

  @Test
  void whenResumeTriggerIsCalled_thenNoContentIsReturned() throws Exception {
    mockMvc.perform(post(TriggerController.TRIGGER_CONTROLLER_BASE_URL + "/DEFAULT/sampleTrigger/resume").contentType(MediaType.APPLICATION_JSON))
      .andExpect(MockMvcResultMatchers.status().isNoContent());

    Mockito.verify(triggerService).resumeTrigger("DEFAULT", "sampleTrigger");
  }

  @Test
  void whenUnscheduleTriggerIsCalled_thenNoContentIsReturned() throws Exception {
    mockMvc.perform(delete(TriggerController.TRIGGER_CONTROLLER_BASE_URL + "/DEFAULT/sampleTrigger").contentType(MediaType.APPLICATION_JSON))
      .andExpect(MockMvcResultMatchers.status().isNoContent());

    Mockito.verify(triggerService).unscheduleTrigger("DEFAULT", "sampleTrigger");
  }

}
