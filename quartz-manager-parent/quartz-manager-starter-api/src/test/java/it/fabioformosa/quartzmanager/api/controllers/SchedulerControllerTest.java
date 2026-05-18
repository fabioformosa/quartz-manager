package it.fabioformosa.quartzmanager.api.controllers;

import it.fabioformosa.quartzmanager.api.QuartManagerApplicationTests;
import it.fabioformosa.quartzmanager.api.controllers.utils.TestUtils;
import it.fabioformosa.quartzmanager.api.dto.SchedulerDTO;
import it.fabioformosa.quartzmanager.api.enums.SchedulerStatus;
import it.fabioformosa.quartzmanager.api.services.SchedulerService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ContextConfiguration(classes = {QuartManagerApplicationTests.class})
@WebMvcTest(controllers = SchedulerController.class, properties = {
  "quartz-manager.jobClassPackages=it.fabioformosa.quartzmanager.jobs"
})
class SchedulerControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private SchedulerService schedulerService;

  @Test
  void whenTheGetIsCalled_thenTheSchedulerServiceIsReturned() throws Exception {
    SchedulerDTO schedulerDTO = SchedulerDTO.builder()
      .name("TEST_SCHEDULER")
      .instanceId("testSchedulerId")
      .status(SchedulerStatus.STOPPED)
      .build();
    Mockito.when(schedulerService.getScheduler()).thenReturn(schedulerDTO);

    mockMvc.perform(get(SchedulerController.SCHEDULER_CONTROLLER_BASE_URL)
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andExpect(MockMvcResultMatchers.content().json(TestUtils.toJson(schedulerDTO)));

    Mockito.verify(schedulerService).getScheduler();
  }

  @Test
  void givenAScheduler_whenStandbyIsCalled_then2xxReturned() throws Exception {
    mockMvc.perform(post(SchedulerController.SCHEDULER_CONTROLLER_BASE_URL + "/standby")
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(MockMvcResultMatchers.status().isNoContent())
      .andExpect(MockMvcResultMatchers.content().string(""));

    Mockito.verify(schedulerService).standby();
  }

  @Test
  void givenAScheduler_whenResumeIsCalled_then2xxReturned() throws Exception {
    mockMvc.perform(post(SchedulerController.SCHEDULER_CONTROLLER_BASE_URL + "/resume")
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(MockMvcResultMatchers.status().isNoContent())
      .andExpect(MockMvcResultMatchers.content().string(""));

    Mockito.verify(schedulerService).start();
  }

  @Test
  void givenAScheduler_whenStartIsCalled_then2xxReturned() throws Exception {
    mockMvc.perform(post(SchedulerController.SCHEDULER_CONTROLLER_BASE_URL + "/start")
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(MockMvcResultMatchers.status().isNoContent())
      .andExpect(MockMvcResultMatchers.content().string(""));

    Mockito.verify(schedulerService).start();
  }

  @Test
  void givenAScheduler_whenStartDelayedIsCalled_then2xxReturned() throws Exception {
    mockMvc.perform(post(SchedulerController.SCHEDULER_CONTROLLER_BASE_URL + "/start-delayed/60")
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(MockMvcResultMatchers.status().isNoContent())
      .andExpect(MockMvcResultMatchers.content().string(""));

    Mockito.verify(schedulerService).startDelayed(60);
  }

  @Test
  void givenAScheduler_whenPauseAllIsCalled_then2xxReturned() throws Exception {
    mockMvc.perform(post(SchedulerController.SCHEDULER_CONTROLLER_BASE_URL + "/pause-all")
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(MockMvcResultMatchers.status().isNoContent())
      .andExpect(MockMvcResultMatchers.content().string(""));

    Mockito.verify(schedulerService).pauseAll();
  }

  @Test
  void givenAScheduler_whenClearIsCalled_then2xxReturned() throws Exception {
    mockMvc.perform(delete(SchedulerController.SCHEDULER_CONTROLLER_BASE_URL)
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(MockMvcResultMatchers.status().isNoContent())
      .andExpect(MockMvcResultMatchers.content().string(""));

    Mockito.verify(schedulerService).clear();
  }

  @Test
  void givenAScheduler_whenShutdownIsCalled_then2xxReturned() throws Exception {
    mockMvc.perform(post(SchedulerController.SCHEDULER_CONTROLLER_BASE_URL + "/shutdown")
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(MockMvcResultMatchers.status().isNoContent())
      .andExpect(MockMvcResultMatchers.content().string(""));

    Mockito.verify(schedulerService).shutdown();
  }

}
