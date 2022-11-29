package it.fabioformosa.quartzmanager.api.controllers;

import it.fabioformosa.quartzmanager.api.QuartManagerApplicationTests;
import it.fabioformosa.quartzmanager.api.controllers.utils.TestUtils;
import it.fabioformosa.quartzmanager.api.jobs.SampleJob;
import it.fabioformosa.quartzmanager.api.services.JobService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ContextConfiguration(classes = {QuartManagerApplicationTests.class})
@WebMvcTest(controllers = SimpleTriggerController.class, properties = {
  "quartz-manager.jobClassPackages=it.fabioformosa.quartzmanager.jobs"
})
class JobControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private JobService jobService;

  @Test
  void whenGetListIsCalled_thenTheSimpleJobIsReturned() throws Exception {
    Mockito.when(jobService.getJobClasses()).thenReturn(List.of(SampleJob.class));

    List<String> expectedJobs = List.of(SampleJob.class.getName());
    mockMvc.perform(get(JobController.JOB_CONTROLLER_BASE_URL)
        .contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk())
      .andExpect(MockMvcResultMatchers.content().json(TestUtils.toJson(expectedJobs)));

    Mockito.verify(jobService, Mockito.times(1)).getJobClasses();
  }

}
