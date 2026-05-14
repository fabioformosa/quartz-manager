package it.fabioformosa.quartzmanager.api.services;

import it.fabioformosa.quartzmanager.api.dto.JobKeyDTO;
import it.fabioformosa.quartzmanager.api.dto.ScheduledJobDTO;
import it.fabioformosa.quartzmanager.api.dto.ScheduledJobInputDTO;
import it.fabioformosa.quartzmanager.api.dto.TriggerKeyDTO;
import it.fabioformosa.quartzmanager.api.exceptions.JobNotFoundException;
import it.fabioformosa.quartzmanager.api.exceptions.ResourceConflictException;
import it.fabioformosa.quartzmanager.api.jobs.SampleJob;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.core.convert.ConversionService;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;


class JobServiceTest {

  @Mock
  private Scheduler scheduler;

  @Mock
  private ConversionService conversionService;

  private JobService schedulerBackedJobService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    schedulerBackedJobService = new JobService("", scheduler, conversionService);
    schedulerBackedJobService.getJobClasses().add(SampleJob.class);
  }

  @Test
  void givenTwoJobClassesInTwoPackages_whenTheJobServiceIsCalled_shouldReturnTwoJobClasses(){
    JobService jobService = new JobService("it.fabioformosa.quartzmanager.api.jobs, it.fabioformosa.samplepackage");
    jobService.initJobClassList();
    Assertions.assertThat(jobService).isNotNull();
    Assertions.assertThat(jobService.getJobClasses()).hasSize(2);
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "it.fabioformosa.quartzmanager.api.jobs",
    "it.fabioformosa.quartzmanager.api.jobs,",
    ",it.fabioformosa.quartzmanager.api.jobs"
  })
  void givenOnePackage_whenTheJobServiceIsCalled_shouldReturnOneJobClasses(String packageStr){
    JobService jobService = new JobService(packageStr);
    jobService.initJobClassList();
    Assertions.assertThat(jobService).isNotNull();
    Assertions.assertThat(jobService.getJobClasses()).hasSize(1);
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "",
    ",",
    ", "
  })
  void givenNoPackages_whenTheJobServiceIsCalled_shouldReturnNoJobClasses(String packageStr){
    JobService jobService = new JobService(packageStr);
    jobService.initJobClassList();
    Assertions.assertThat(jobService).isNotNull();
    Assertions.assertThat(jobService.getJobClasses()).isEmpty();
  }

  @Test
  void givenScheduledJobs_whenFetched_thenReturnsConvertedJobDtos() throws SchedulerException {
    JobKey jobKey = JobKey.jobKey("job", "group");
    JobDetail jobDetail = org.quartz.JobBuilder.newJob(SampleJob.class)
      .withIdentity(jobKey)
      .withDescription("sample")
      .storeDurably(true)
      .requestRecovery(true)
      .usingJobData("key", "value")
      .build();
    Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger", "triggers").forJob(jobKey).build();
    Mockito.when(scheduler.getJobKeys(any())).thenReturn(Set.of(jobKey));
    Mockito.when(scheduler.getJobDetail(jobKey)).thenReturn(jobDetail);
    Mockito.doReturn(List.of(trigger)).when(scheduler).getTriggersOfJob(jobKey);
    mockKeyConversions(jobKey, trigger.getKey());

    List<ScheduledJobDTO> scheduledJobs = schedulerBackedJobService.fetchScheduledJobs();

    Assertions.assertThat(scheduledJobs).hasSize(1);
    Assertions.assertThat(scheduledJobs.get(0).getJobClassName()).isEqualTo(SampleJob.class.getName());
    Assertions.assertThat(scheduledJobs.get(0).getDescription()).isEqualTo("sample");
    Assertions.assertThat(scheduledJobs.get(0).isDurable()).isTrue();
    Assertions.assertThat(scheduledJobs.get(0).isRequestsRecovery()).isTrue();
    Assertions.assertThat((Map<String, Object>) scheduledJobs.get(0).getJobDataMap()).containsEntry("key", "value");
    Assertions.assertThat(scheduledJobs.get(0).getTriggerKeys()).hasSize(1);
  }

  @Test
  void givenMissingScheduledJob_whenFetched_thenThrowsNotFound() throws SchedulerException {
    JobKey jobKey = JobKey.jobKey("job", "group");
    Mockito.when(scheduler.checkExists(jobKey)).thenReturn(false);

    Assertions.assertThatThrownBy(() -> schedulerBackedJobService.getScheduledJob("group", "job"))
      .isInstanceOf(JobNotFoundException.class);
  }

  @Test
  void givenExistingJob_whenCreated_thenThrowsConflict() throws SchedulerException {
    JobKey jobKey = JobKey.jobKey("job", "group");
    Mockito.when(scheduler.checkExists(jobKey)).thenReturn(true);

    ScheduledJobInputDTO inputDTO = ScheduledJobInputDTO.builder().jobClass(SampleJob.class.getName()).build();

    Assertions.assertThatThrownBy(() -> schedulerBackedJobService.createJob("group", "job", inputDTO))
      .isInstanceOf(ResourceConflictException.class);
  }

  @Test
  void givenNewJob_whenCreated_thenAddsDurableJobAndReturnsDto() throws SchedulerException, ClassNotFoundException {
    JobKey jobKey = JobKey.jobKey("job", "group");
    Mockito.when(scheduler.checkExists(jobKey)).thenReturn(false);
    Mockito.when(scheduler.getJobDetail(jobKey)).thenAnswer(invocation -> org.quartz.JobBuilder.newJob(SampleJob.class).withIdentity(jobKey).storeDurably(true).build());
    Mockito.when(scheduler.getTriggersOfJob(jobKey)).thenReturn(List.of());
    mockKeyConversions(jobKey, null);
    ArgumentCaptor<JobDetail> jobDetailCaptor = ArgumentCaptor.forClass(JobDetail.class);
    ScheduledJobInputDTO inputDTO = ScheduledJobInputDTO.builder()
      .jobClass(SampleJob.class.getName())
      .description("sample")
      .durable(true)
      .requestsRecovery(true)
      .jobDataMap(Map.of("key", "value"))
      .build();

    schedulerBackedJobService.createJob("group", "job", inputDTO);

    Mockito.verify(scheduler).addJob(jobDetailCaptor.capture(), eq(false));
    JobDetail createdJob = jobDetailCaptor.getValue();
    Assertions.assertThat(createdJob.getKey()).isEqualTo(jobKey);
    Assertions.assertThat(createdJob.getJobClass()).isEqualTo(SampleJob.class);
    Assertions.assertThat(createdJob.getDescription()).isEqualTo("sample");
    Assertions.assertThat(createdJob.isDurable()).isTrue();
    Assertions.assertThat(createdJob.requestsRecovery()).isTrue();
    Assertions.assertThat(createdJob.getJobDataMap().getString("key")).isEqualTo("value");
  }

  @Test
  void givenExistingJob_whenUpdated_thenReplacesJob() throws SchedulerException, ClassNotFoundException, JobNotFoundException {
    JobKey jobKey = JobKey.jobKey("job", "group");
    Mockito.when(scheduler.checkExists(jobKey)).thenReturn(true);
    Mockito.when(scheduler.getJobDetail(jobKey)).thenAnswer(invocation -> org.quartz.JobBuilder.newJob(SampleJob.class).withIdentity(jobKey).storeDurably(true).build());
    Mockito.when(scheduler.getTriggersOfJob(jobKey)).thenReturn(List.of());
    mockKeyConversions(jobKey, null);
    ScheduledJobInputDTO inputDTO = ScheduledJobInputDTO.builder().jobClass(SampleJob.class.getName()).build();

    schedulerBackedJobService.updateJob("group", "job", inputDTO);

    Mockito.verify(scheduler).addJob(any(JobDetail.class), eq(true));
  }

  @Test
  void givenExistingJob_whenTriggeredAndDeleted_thenDelegatesToScheduler() throws SchedulerException, JobNotFoundException {
    JobKey jobKey = JobKey.jobKey("job", "group");
    Mockito.when(scheduler.checkExists(jobKey)).thenReturn(true);

    schedulerBackedJobService.triggerJob("group", "job");
    schedulerBackedJobService.deleteJob("group", "job");

    Mockito.verify(scheduler).triggerJob(jobKey);
    Mockito.verify(scheduler).deleteJob(jobKey);
  }

  @Test
  void givenMissingJob_whenDeleted_thenThrowsNotFound() throws SchedulerException {
    JobKey jobKey = JobKey.jobKey("job", "group");
    Mockito.when(scheduler.checkExists(jobKey)).thenReturn(false);

    Assertions.assertThatThrownBy(() -> schedulerBackedJobService.deleteJob("group", "job"))
      .isInstanceOf(JobNotFoundException.class);
  }

  private void mockKeyConversions(JobKey jobKey, TriggerKey triggerKey) {
    Mockito.when(conversionService.convert(jobKey, JobKeyDTO.class))
      .thenReturn(JobKeyDTO.builder().name(jobKey.getName()).group(jobKey.getGroup()).build());
    if (triggerKey != null) {
      Mockito.when(conversionService.convert(triggerKey, TriggerKeyDTO.class))
        .thenReturn(TriggerKeyDTO.builder().name(triggerKey.getName()).group(triggerKey.getGroup()).build());
    }
  }

}
