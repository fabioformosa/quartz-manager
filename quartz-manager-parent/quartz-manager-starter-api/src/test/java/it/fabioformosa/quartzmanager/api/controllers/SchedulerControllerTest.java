package it.fabioformosa.quartzmanager.api.controllers;

import it.fabioformosa.quartzmanager.api.QuartManagerApplicationTests;
import it.fabioformosa.quartzmanager.api.controllers.utils.TestUtils;
import it.fabioformosa.quartzmanager.api.dto.SchedulerDTO;
import it.fabioformosa.quartzmanager.api.enums.SchedulerStatus;
import it.fabioformosa.quartzmanager.api.services.SchedulerService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ContextConfiguration(classes = {QuartManagerApplicationTests.class})
@WebMvcTest(controllers = SimpleTriggerController.class, properties = {
  "quartz-manager.jobClassPackages=it.fabioformosa.quartzmanager.jobs"
})
class SchedulerControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
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
  void givenAScheduler_whenTheGetPausedIsCalled_then2xxReturned() throws Exception {
    mockMvc.perform(get(SchedulerController.SCHEDULER_CONTROLLER_BASE_URL + "/pause")
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(MockMvcResultMatchers.status().isNoContent())
      .andExpect(MockMvcResultMatchers.content().string(""));

    Mockito.verify(schedulerService).standby();
  }

  @Test
  void givenAScheduler_whenTheGetResumedIsCalled_then2xxReturned() throws Exception {
    mockMvc.perform(get(SchedulerController.SCHEDULER_CONTROLLER_BASE_URL + "/resume")
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(MockMvcResultMatchers.status().isNoContent())
      .andExpect(MockMvcResultMatchers.content().string(""));

    Mockito.verify(schedulerService).start();
  }

  @Test
  void givenAScheduler_whenTheGetRunIsCalled_then2xxReturned() throws Exception {
    mockMvc.perform(get(SchedulerController.SCHEDULER_CONTROLLER_BASE_URL + "/run")
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(MockMvcResultMatchers.status().isNoContent())
      .andExpect(MockMvcResultMatchers.content().string(""));

    Mockito.verify(schedulerService).start();
  }

  @Test
  void givenAScheduler_whenTheGetStoppedIsCalled_then2xxReturned() throws Exception {
    mockMvc.perform(get(SchedulerController.SCHEDULER_CONTROLLER_BASE_URL + "/stop")
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(MockMvcResultMatchers.status().isNoContent())
      .andExpect(MockMvcResultMatchers.content().string(""));

    Mockito.verify(schedulerService).shutdown();
  }

}
