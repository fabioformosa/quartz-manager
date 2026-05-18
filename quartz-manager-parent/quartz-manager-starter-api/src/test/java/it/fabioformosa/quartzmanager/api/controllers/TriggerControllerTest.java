package it.fabioformosa.quartzmanager.api.controllers;

import it.fabioformosa.quartzmanager.api.QuartManagerApplicationTests;
import it.fabioformosa.quartzmanager.api.controllers.utils.TestUtils;
import it.fabioformosa.quartzmanager.api.dto.TriggerDTO;
import it.fabioformosa.quartzmanager.api.dto.TriggerInputDTO;
import it.fabioformosa.quartzmanager.api.dto.TriggerKeyDTO;
import it.fabioformosa.quartzmanager.api.dto.TriggerType;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

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
  void givenATriggerInputDTO_whenPosted_thenTriggerIsCreated() throws Exception {
    TriggerInputDTO triggerInputDTO = TriggerInputDTO.builder()
      .triggerType(TriggerType.CRON)
      .jobClass("it.fabioformosa.quartzmanager.api.jobs.SampleJob")
      .cronExpression("0 0/5 * * * ?")
      .misfireInstruction("FIRE_AND_PROCEED")
      .build();
    TriggerDTO triggerDTO = TriggerDTO.builder()
      .triggerKeyDTO(TriggerKeyDTO.builder().name("cronTrigger").group("DEFAULT").build())
      .type("CronTriggerImpl")
      .build();
    Mockito.when(triggerService.scheduleTrigger("DEFAULT", "cronTrigger", triggerInputDTO)).thenReturn(triggerDTO);

    mockMvc.perform(post(TriggerController.TRIGGER_CONTROLLER_BASE_URL + "/DEFAULT/cronTrigger")
        .contentType(MediaType.APPLICATION_JSON)
        .content(TestUtils.toJson(triggerInputDTO)))
      .andExpect(MockMvcResultMatchers.status().isCreated())
      .andExpect(MockMvcResultMatchers.content().json(TestUtils.toJson(triggerDTO)));
  }

  @Test
  void givenATriggerInputDTO_whenPut_thenTriggerIsRescheduled() throws Exception {
    TriggerInputDTO triggerInputDTO = TriggerInputDTO.builder()
      .triggerType(TriggerType.CALENDAR_INTERVAL)
      .jobClass("it.fabioformosa.quartzmanager.api.jobs.SampleJob")
      .repeatInterval(2L)
      .repeatIntervalUnit("DAY")
      .misfireInstruction("DO_NOTHING")
      .build();
    TriggerDTO triggerDTO = TriggerDTO.builder()
      .triggerKeyDTO(TriggerKeyDTO.builder().name("calendarTrigger").group("DEFAULT").build())
      .type("CalendarIntervalTriggerImpl")
      .build();
    Mockito.when(triggerService.rescheduleTrigger("DEFAULT", "calendarTrigger", triggerInputDTO)).thenReturn(triggerDTO);

    mockMvc.perform(put(TriggerController.TRIGGER_CONTROLLER_BASE_URL + "/DEFAULT/calendarTrigger")
        .contentType(MediaType.APPLICATION_JSON)
        .content(TestUtils.toJson(triggerInputDTO)))
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
  void whenResetTriggerFromErrorStateIsCalled_thenNoContentIsReturned() throws Exception {
    mockMvc.perform(post(TriggerController.TRIGGER_CONTROLLER_BASE_URL + "/DEFAULT/sampleTrigger/reset-error").contentType(MediaType.APPLICATION_JSON))
      .andExpect(MockMvcResultMatchers.status().isNoContent());

    Mockito.verify(triggerService).resetTriggerFromErrorState("DEFAULT", "sampleTrigger");
  }

  @Test
  void whenUnscheduleTriggerIsCalled_thenNoContentIsReturned() throws Exception {
    mockMvc.perform(delete(TriggerController.TRIGGER_CONTROLLER_BASE_URL + "/DEFAULT/sampleTrigger").contentType(MediaType.APPLICATION_JSON))
      .andExpect(MockMvcResultMatchers.status().isNoContent());

    Mockito.verify(triggerService).unscheduleTrigger("DEFAULT", "sampleTrigger");
  }

}
