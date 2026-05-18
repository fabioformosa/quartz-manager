package it.fabioformosa.quartzmanager.api.controllers;

import it.fabioformosa.quartzmanager.api.QuartManagerApplicationTests;
import it.fabioformosa.quartzmanager.api.controllers.utils.TestUtils;
import it.fabioformosa.quartzmanager.api.dto.CurrentExecutionDTO;
import it.fabioformosa.quartzmanager.api.dto.JobKeyDTO;
import it.fabioformosa.quartzmanager.api.dto.TriggerKeyDTO;
import it.fabioformosa.quartzmanager.api.services.ExecutionService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ContextConfiguration(classes = {QuartManagerApplicationTests.class})
@WebMvcTest(controllers = ExecutionController.class, properties = {
  "quartz-manager.jobClassPackages=it.fabioformosa.quartzmanager.jobs"
})
class ExecutionControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private ExecutionService executionService;

  @Test
  void whenGetCurrentExecutionsIsCalled_thenCurrentExecutionsAreReturned() throws Exception {
    CurrentExecutionDTO currentExecutionDTO = CurrentExecutionDTO.builder()
      .fireInstanceId("fire-1")
      .jobKeyDTO(JobKeyDTO.builder().group("DEFAULT").name("sampleJob").build())
      .triggerKeyDTO(TriggerKeyDTO.builder().group("DEFAULT").name("sampleTrigger").build())
      .runTime(1500L)
      .node("node-1")
      .build();
    Mockito.when(executionService.getCurrentExecutions()).thenReturn(List.of(currentExecutionDTO));

    mockMvc.perform(get(ExecutionController.EXECUTION_CONTROLLER_BASE_URL + "/current")
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andExpect(MockMvcResultMatchers.content().json(TestUtils.toJson(List.of(currentExecutionDTO))));

    Mockito.verify(executionService).getCurrentExecutions();
  }

  @Test
  void whenGetRecoveringExecutionsIsCalled_thenRecoveringExecutionsAreReturned() throws Exception {
    CurrentExecutionDTO recoveringExecutionDTO = CurrentExecutionDTO.builder()
      .fireInstanceId("fire-2")
      .jobKeyDTO(JobKeyDTO.builder().group("DEFAULT").name("recoveringJob").build())
      .triggerKeyDTO(TriggerKeyDTO.builder().group("DEFAULT").name("recoveringTrigger").build())
      .recovering(true)
      .node("node-1")
      .build();
    Mockito.when(executionService.getRecoveringExecutions()).thenReturn(List.of(recoveringExecutionDTO));

    mockMvc.perform(get(ExecutionController.EXECUTION_CONTROLLER_BASE_URL + "/recovering")
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andExpect(MockMvcResultMatchers.content().json(TestUtils.toJson(List.of(recoveringExecutionDTO))));

    Mockito.verify(executionService).getRecoveringExecutions();
  }
}
