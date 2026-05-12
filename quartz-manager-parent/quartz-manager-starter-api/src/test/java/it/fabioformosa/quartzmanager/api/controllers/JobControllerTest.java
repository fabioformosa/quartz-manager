package it.fabioformosa.quartzmanager.api.controllers;

import it.fabioformosa.quartzmanager.api.QuartManagerApplicationTests;
import it.fabioformosa.quartzmanager.api.controllers.utils.TestUtils;
import it.fabioformosa.quartzmanager.api.dto.JobKeyDTO;
import it.fabioformosa.quartzmanager.api.dto.ScheduledJobDTO;
import it.fabioformosa.quartzmanager.api.dto.ScheduledJobInputDTO;
import it.fabioformosa.quartzmanager.api.jobs.SampleJob;
import it.fabioformosa.quartzmanager.api.services.JobService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@ContextConfiguration(classes = {QuartManagerApplicationTests.class})
@WebMvcTest(controllers = JobController.class, properties = {
  "quartz-manager.jobClassPackages=it.fabioformosa.quartzmanager.jobs"
})
class JobControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private JobService jobService;

  @Test
  void whenGetListIsCalled_thenTheSimpleJobIsReturned() throws Exception {
    Mockito.when(jobService.getJobClasses()).thenReturn(List.of(SampleJob.class));

    List<String> expectedJobs = List.of(SampleJob.class.getName());
    mockMvc.perform(get(JobController.JOB_CLASSES_CONTROLLER_BASE_URL)
        .contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk())
      .andExpect(MockMvcResultMatchers.content().json(TestUtils.toJson(expectedJobs)));

    Mockito.verify(jobService, Mockito.times(1)).getJobClasses();
  }

  @Test
  void whenGetScheduledJobsIsCalled_thenScheduledJobsAreReturned() throws Exception {
    ScheduledJobDTO scheduledJobDTO = ScheduledJobDTO.builder()
      .jobKeyDTO(JobKeyDTO.builder().name("sampleJob").group("DEFAULT").build())
      .jobClassName(SampleJob.class.getName())
      .build();
    Mockito.when(jobService.fetchScheduledJobs()).thenReturn(List.of(scheduledJobDTO));

    mockMvc.perform(get(JobController.JOB_CONTROLLER_BASE_URL)
        .contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk())
      .andExpect(MockMvcResultMatchers.content().json(TestUtils.toJson(List.of(scheduledJobDTO))));

    Mockito.verify(jobService).fetchScheduledJobs();
  }

  @Test
  void whenTriggerJobIsCalled_thenNoContentIsReturned() throws Exception {
    mockMvc.perform(post(JobController.JOB_CONTROLLER_BASE_URL + "/DEFAULT/sampleJob/trigger")
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(MockMvcResultMatchers.status().isNoContent());

    Mockito.verify(jobService).triggerJob("DEFAULT", "sampleJob");
  }

  @Test
  void whenCreateJobIsCalled_thenCreatedJobIsReturned() throws Exception {
    ScheduledJobInputDTO inputDTO = ScheduledJobInputDTO.builder()
      .jobClass(SampleJob.class.getName())
      .durable(true)
      .build();
    ScheduledJobDTO scheduledJobDTO = ScheduledJobDTO.builder()
      .jobKeyDTO(JobKeyDTO.builder().name("sampleJob").group("DEFAULT").build())
      .jobClassName(SampleJob.class.getName())
      .durable(true)
      .build();
    Mockito.when(jobService.createJob("DEFAULT", "sampleJob", inputDTO)).thenReturn(scheduledJobDTO);

    mockMvc.perform(post(JobController.JOB_CONTROLLER_BASE_URL + "/DEFAULT/sampleJob")
        .contentType(MediaType.APPLICATION_JSON)
        .content(TestUtils.toJson(inputDTO)))
      .andExpect(MockMvcResultMatchers.status().isCreated())
      .andExpect(MockMvcResultMatchers.content().json(TestUtils.toJson(scheduledJobDTO)));

    Mockito.verify(jobService).createJob("DEFAULT", "sampleJob", inputDTO);
  }

  @Test
  void whenUpdateJobIsCalled_thenUpdatedJobIsReturned() throws Exception {
    ScheduledJobInputDTO inputDTO = ScheduledJobInputDTO.builder()
      .jobClass(SampleJob.class.getName())
      .durable(true)
      .build();
    ScheduledJobDTO scheduledJobDTO = ScheduledJobDTO.builder()
      .jobKeyDTO(JobKeyDTO.builder().name("sampleJob").group("DEFAULT").build())
      .jobClassName(SampleJob.class.getName())
      .durable(true)
      .build();
    Mockito.when(jobService.updateJob("DEFAULT", "sampleJob", inputDTO)).thenReturn(scheduledJobDTO);

    mockMvc.perform(put(JobController.JOB_CONTROLLER_BASE_URL + "/DEFAULT/sampleJob")
        .contentType(MediaType.APPLICATION_JSON)
        .content(TestUtils.toJson(inputDTO)))
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andExpect(MockMvcResultMatchers.content().json(TestUtils.toJson(scheduledJobDTO)));

    Mockito.verify(jobService).updateJob("DEFAULT", "sampleJob", inputDTO);
  }

  @Test
  void whenDeleteJobIsCalled_thenNoContentIsReturned() throws Exception {
    mockMvc.perform(delete(JobController.JOB_CONTROLLER_BASE_URL + "/DEFAULT/sampleJob")
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(MockMvcResultMatchers.status().isNoContent());

    Mockito.verify(jobService).deleteJob("DEFAULT", "sampleJob");
  }

}
