package it.fabioformosa.quartzmanager.api.services;

import it.fabioformosa.quartzmanager.api.dto.JobKeyDTO;
import it.fabioformosa.quartzmanager.api.dto.MisfireInstruction;
import it.fabioformosa.quartzmanager.api.dto.TriggerDTO;
import it.fabioformosa.quartzmanager.api.dto.TriggerInputDTO;
import it.fabioformosa.quartzmanager.api.dto.TriggerKeyDTO;
import it.fabioformosa.quartzmanager.api.dto.TriggerType;
import it.fabioformosa.quartzmanager.api.exceptions.ResourceConflictException;
import it.fabioformosa.quartzmanager.api.exceptions.TriggerNotFoundException;
import it.fabioformosa.quartzmanager.api.jobs.SampleJob;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.quartz.CalendarIntervalTrigger;
import org.quartz.CronTrigger;
import org.quartz.DailyTimeIntervalTrigger;
import org.quartz.DateBuilder;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.springframework.core.convert.ConversionService;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

class TriggerServiceTest {

  @InjectMocks
  private TriggerService triggerService;

  @Mock
  private Scheduler scheduler;

  @Mock
  private ConversionService conversionService;

  @Mock
  private JobService jobService;

  @BeforeEach
  void setUp() throws ClassNotFoundException {
    MockitoAnnotations.openMocks(this);
    Mockito.doReturn(SampleJob.class).when(jobService).getEligibleJobClass(SampleJob.class.getName());
  }

  @Test
  void givenATrigger_whenTheyAreFecthed_TheServiceReturnsTheDtos() throws SchedulerException {
    String triggerTestName = "triggerTest";
    Mockito.when(scheduler.getTriggerKeys(any())).thenReturn(Set.of(TriggerKey.triggerKey(triggerTestName)));
    Mockito.when(conversionService.convert(any(TriggerKey.class), eq(TriggerKeyDTO.class)))
      .thenReturn(TriggerKeyDTO.builder().name(triggerTestName).build());

    List<TriggerKeyDTO> triggerKeyDTOs = triggerService.fetchTriggers();
    Assertions.assertThat(triggerKeyDTOs).hasSize(1);
    Assertions.assertThat(triggerKeyDTOs.get(0).getName()).isEqualTo(triggerTestName);
  }

  @Test
  void givenMissingTrigger_whenFetched_thenThrowsNotFound() throws SchedulerException {
    Mockito.when(scheduler.getTrigger(TriggerKey.triggerKey("trigger", "group"))).thenReturn(null);

    Assertions.assertThatThrownBy(() -> triggerService.getTrigger("group", "trigger"))
      .isInstanceOf(TriggerNotFoundException.class);
  }

  @Test
  void givenExistingTrigger_whenFetched_thenReturnsStateAndSimpleDetails() throws SchedulerException, TriggerNotFoundException {
    TriggerKey triggerKey = TriggerKey.triggerKey("trigger", "group");
    SimpleTrigger trigger = org.quartz.TriggerBuilder.newTrigger()
      .withIdentity(triggerKey)
      .withSchedule(org.quartz.SimpleScheduleBuilder.simpleSchedule().withIntervalInMilliseconds(5000).withRepeatCount(3))
      .build();
    TriggerDTO convertedDTO = new TriggerDTO();
    Mockito.when(scheduler.getTrigger(triggerKey)).thenReturn(trigger);
    Mockito.when(scheduler.getTriggerState(triggerKey)).thenReturn(Trigger.TriggerState.NORMAL);
    Mockito.when(conversionService.convert(trigger, TriggerDTO.class)).thenReturn(convertedDTO);

    TriggerDTO result = triggerService.getTrigger("group", "trigger");

    Assertions.assertThat(result.getState()).isEqualTo("NORMAL");
    Assertions.assertThat(result.getRepeatInterval()).isEqualTo(5000L);
    Assertions.assertThat(result.getRepeatCount()).isEqualTo(3);
  }

  @Test
  void givenExistingTriggerKey_whenScheduled_thenThrowsConflict() throws SchedulerException {
    TriggerKey triggerKey = TriggerKey.triggerKey("trigger", "group");
    Mockito.when(scheduler.checkExists(triggerKey)).thenReturn(true);

    TriggerInputDTO inputDTO = TriggerInputDTO.builder().triggerType(TriggerType.SIMPLE).jobClass(SampleJob.class.getName()).build();

    Assertions.assertThatThrownBy(() -> triggerService.scheduleTrigger("group", "trigger", inputDTO))
      .isInstanceOf(ResourceConflictException.class);
  }

  @Test
  void givenMissingTargetJob_whenTriggerIsScheduled_thenThrowsConflict() throws SchedulerException {
    TriggerKey triggerKey = TriggerKey.triggerKey("trigger", "group");
    JobKey jobKey = JobKey.jobKey("job", "jobs");
    Mockito.when(scheduler.checkExists(triggerKey)).thenReturn(false);
    Mockito.when(scheduler.checkExists(jobKey)).thenReturn(false);

    TriggerInputDTO inputDTO = TriggerInputDTO.builder()
      .triggerType(TriggerType.SIMPLE)
      .jobKey(JobKeyDTO.builder().name("job").group("jobs").build())
      .build();

    Assertions.assertThatThrownBy(() -> triggerService.scheduleTrigger("group", "trigger", inputDTO))
      .isInstanceOf(ResourceConflictException.class);
  }

  @Test
  void givenSimpleTriggerInputWithJobClass_whenScheduled_thenBuildsSimpleTriggerAndJobDetail() throws SchedulerException, ClassNotFoundException, ParseException {
    Mockito.when(scheduler.checkExists(TriggerKey.triggerKey("trigger", "group"))).thenReturn(false);
    Mockito.when(conversionService.convert(any(Trigger.class), eq(TriggerDTO.class))).thenReturn(new TriggerDTO());
    ArgumentCaptor<org.quartz.JobDetail> jobDetailCaptor = ArgumentCaptor.forClass(org.quartz.JobDetail.class);
    ArgumentCaptor<Trigger> triggerCaptor = ArgumentCaptor.forClass(Trigger.class);

    TriggerInputDTO inputDTO = TriggerInputDTO.builder()
      .triggerType(TriggerType.SIMPLE)
      .jobClass(SampleJob.class.getName())
      .description("sample")
      .priority(7)
      .repeatInterval(2000L)
      .repeatCount(5)
      .misfireInstruction(MisfireInstruction.MISFIRE_INSTRUCTION_FIRE_NOW.name())
      .jobDataMap(Map.of("key", "value"))
      .build();

    TriggerDTO result = triggerService.scheduleTrigger("group", "trigger", inputDTO);

    Mockito.verify(scheduler).scheduleJob(jobDetailCaptor.capture(), triggerCaptor.capture());
    Assertions.assertThat(jobDetailCaptor.getValue().getJobClass()).isEqualTo(SampleJob.class);
    Assertions.assertThat(triggerCaptor.getValue()).isInstanceOf(SimpleTrigger.class);
    SimpleTrigger trigger = (SimpleTrigger) triggerCaptor.getValue();
    Assertions.assertThat(trigger.getPriority()).isEqualTo(7);
    Assertions.assertThat(trigger.getDescription()).isEqualTo("sample");
    Assertions.assertThat(trigger.getRepeatInterval()).isEqualTo(2000L);
    Assertions.assertThat(trigger.getRepeatCount()).isEqualTo(5);
    Assertions.assertThat(result.getRepeatInterval()).isEqualTo(2000L);
    Assertions.assertThat(result.getRepeatCount()).isEqualTo(5);
  }

  @Test
  void givenCronTriggerInput_whenScheduledForExistingJob_thenBuildsCronTrigger() throws SchedulerException, ClassNotFoundException, ParseException {
    TriggerKey triggerKey = TriggerKey.triggerKey("trigger", "group");
    JobKey jobKey = JobKey.jobKey("job", "jobs");
    Mockito.when(scheduler.checkExists(triggerKey)).thenReturn(false);
    Mockito.when(scheduler.checkExists(jobKey)).thenReturn(true);
    Mockito.when(conversionService.convert(any(Trigger.class), eq(TriggerDTO.class))).thenReturn(new TriggerDTO());
    ArgumentCaptor<Trigger> triggerCaptor = ArgumentCaptor.forClass(Trigger.class);

    TriggerInputDTO inputDTO = TriggerInputDTO.builder()
      .triggerType(TriggerType.CRON)
      .jobKey(JobKeyDTO.builder().name("job").group("jobs").build())
      .cronExpression("0 0 12 * * ?")
      .timeZone("UTC")
      .misfireInstruction("DO_NOTHING")
      .build();

    TriggerDTO result = triggerService.scheduleTrigger("group", "trigger", inputDTO);

    Mockito.verify(scheduler).scheduleJob(triggerCaptor.capture());
    Assertions.assertThat(triggerCaptor.getValue()).isInstanceOf(CronTrigger.class);
    CronTrigger trigger = (CronTrigger) triggerCaptor.getValue();
    Assertions.assertThat(trigger.getJobKey()).isEqualTo(jobKey);
    Assertions.assertThat(trigger.getCronExpression()).isEqualTo("0 0 12 * * ?");
    Assertions.assertThat(trigger.getTimeZone()).isEqualTo(TimeZone.getTimeZone("UTC"));
    Assertions.assertThat(result.getCronExpression()).isEqualTo("0 0 12 * * ?");
  }

  @Test
  void givenDailyTriggerInput_whenScheduled_thenBuildsDailyTrigger() throws SchedulerException, ClassNotFoundException, ParseException {
    Mockito.when(scheduler.checkExists(TriggerKey.triggerKey("daily", "group"))).thenReturn(false);
    Mockito.when(conversionService.convert(any(Trigger.class), eq(TriggerDTO.class))).thenReturn(new TriggerDTO());
    ArgumentCaptor<Trigger> triggerCaptor = ArgumentCaptor.forClass(Trigger.class);

    TriggerInputDTO inputDTO = TriggerInputDTO.builder()
      .triggerType(TriggerType.DAILY_TIME_INTERVAL)
      .jobClass(SampleJob.class.getName())
      .repeatInterval(2L)
      .repeatIntervalUnit(DateBuilder.IntervalUnit.HOUR.name())
      .startTimeOfDay("09:15")
      .endTimeOfDay("17:45:30")
      .daysOfWeek(Set.of(java.util.Calendar.MONDAY, java.util.Calendar.WEDNESDAY))
      .misfireInstruction("IGNORE_MISFIRES")
      .build();

    TriggerDTO result = triggerService.scheduleTrigger("group", "daily", inputDTO);

    Mockito.verify(scheduler).scheduleJob(any(org.quartz.JobDetail.class), triggerCaptor.capture());
    Assertions.assertThat(triggerCaptor.getValue()).isInstanceOf(DailyTimeIntervalTrigger.class);
    DailyTimeIntervalTrigger trigger = (DailyTimeIntervalTrigger) triggerCaptor.getValue();
    Assertions.assertThat(trigger.getRepeatInterval()).isEqualTo(2);
    Assertions.assertThat(trigger.getRepeatIntervalUnit()).isEqualTo(DateBuilder.IntervalUnit.HOUR);
    Assertions.assertThat(result.getStartTimeOfDay()).isEqualTo("09:15:00");
    Assertions.assertThat(result.getEndTimeOfDay()).isEqualTo("17:45:30");
    Assertions.assertThat(result.getDaysOfWeek()).contains(java.util.Calendar.MONDAY, java.util.Calendar.WEDNESDAY);
  }

  @Test
  void givenCalendarIntervalTriggerInput_whenScheduled_thenBuildsCalendarIntervalTrigger() throws SchedulerException, ClassNotFoundException, ParseException {
    Date startDate = new Date(System.currentTimeMillis() + 1000);
    Date endDate = new Date(startDate.getTime() + 10000);
    Mockito.when(scheduler.checkExists(TriggerKey.triggerKey("calendar", "group"))).thenReturn(false);
    Mockito.when(conversionService.convert(any(Trigger.class), eq(TriggerDTO.class))).thenReturn(new TriggerDTO());
    ArgumentCaptor<Trigger> triggerCaptor = ArgumentCaptor.forClass(Trigger.class);

    TriggerInputDTO inputDTO = TriggerInputDTO.builder()
      .triggerType(TriggerType.CALENDAR_INTERVAL)
      .jobClass(SampleJob.class.getName())
      .startDate(startDate)
      .endDate(endDate)
      .calendarName("holidays")
      .repeatInterval(3L)
      .repeatIntervalUnit(DateBuilder.IntervalUnit.WEEK.name())
      .timeZone("UTC")
      .preserveHourOfDayAcrossDaylightSavings(true)
      .skipDayIfHourDoesNotExist(true)
      .misfireInstruction("FIRE_AND_PROCEED")
      .build();

    TriggerDTO result = triggerService.scheduleTrigger("group", "calendar", inputDTO);

    Mockito.verify(scheduler).scheduleJob(any(org.quartz.JobDetail.class), triggerCaptor.capture());
    Assertions.assertThat(triggerCaptor.getValue()).isInstanceOf(CalendarIntervalTrigger.class);
    CalendarIntervalTrigger trigger = (CalendarIntervalTrigger) triggerCaptor.getValue();
    Assertions.assertThat(trigger.getCalendarName()).isEqualTo("holidays");
    Assertions.assertThat(trigger.getRepeatInterval()).isEqualTo(3);
    Assertions.assertThat(trigger.getRepeatIntervalUnit()).isEqualTo(DateBuilder.IntervalUnit.WEEK);
    Assertions.assertThat(trigger.isPreserveHourOfDayAcrossDaylightSavings()).isTrue();
    Assertions.assertThat(trigger.isSkipDayIfHourDoesNotExist()).isTrue();
    Assertions.assertThat(result.getTimeZone()).isEqualTo("UTC");
  }

  @Test
  void givenExistingTrigger_whenRescheduled_thenKeepsExistingJob() throws SchedulerException, ParseException, TriggerNotFoundException {
    TriggerKey triggerKey = TriggerKey.triggerKey("trigger", "group");
    JobKey jobKey = JobKey.jobKey("job", "jobs");
    Trigger existingTrigger = org.quartz.TriggerBuilder.newTrigger().withIdentity(triggerKey).forJob(jobKey).build();
    Mockito.when(scheduler.getTrigger(triggerKey)).thenReturn(existingTrigger);
    Mockito.when(conversionService.convert(any(Trigger.class), eq(TriggerDTO.class))).thenReturn(new TriggerDTO());
    ArgumentCaptor<Trigger> triggerCaptor = ArgumentCaptor.forClass(Trigger.class);

    TriggerInputDTO inputDTO = TriggerInputDTO.builder()
      .triggerType(TriggerType.SIMPLE)
      .repeatInterval(1000L)
      .repeatCount(1)
      .build();

    triggerService.rescheduleTrigger("group", "trigger", inputDTO);

    Mockito.verify(scheduler).rescheduleJob(eq(triggerKey), triggerCaptor.capture());
    Assertions.assertThat(triggerCaptor.getValue().getJobKey()).isEqualTo(jobKey);
  }

  @Test
  void givenMissingTrigger_whenRescheduled_thenThrowsNotFound() throws SchedulerException {
    Mockito.when(scheduler.getTrigger(TriggerKey.triggerKey("trigger", "group"))).thenReturn(null);

    Assertions.assertThatThrownBy(() -> triggerService.rescheduleTrigger("group", "trigger", TriggerInputDTO.builder().triggerType(TriggerType.SIMPLE).build()))
      .isInstanceOf(TriggerNotFoundException.class);
  }

  @Test
  void givenExistingTrigger_whenPausedResumedAndUnscheduled_thenDelegatesToScheduler() throws SchedulerException, TriggerNotFoundException {
    TriggerKey triggerKey = TriggerKey.triggerKey("trigger", "group");
    Mockito.when(scheduler.checkExists(triggerKey)).thenReturn(true);

    triggerService.pauseTrigger("group", "trigger");
    triggerService.resumeTrigger("group", "trigger");
    triggerService.unscheduleTrigger("group", "trigger");

    Mockito.verify(scheduler).pauseTrigger(triggerKey);
    Mockito.verify(scheduler).resumeTrigger(triggerKey);
    Mockito.verify(scheduler).unscheduleJob(triggerKey);
  }

  @Test
  void givenMissingTrigger_whenPaused_thenThrowsNotFound() throws SchedulerException {
    TriggerKey triggerKey = TriggerKey.triggerKey("trigger", "group");
    Mockito.when(scheduler.checkExists(triggerKey)).thenReturn(false);

    Assertions.assertThatThrownBy(() -> triggerService.pauseTrigger("group", "trigger"))
      .isInstanceOf(TriggerNotFoundException.class);
  }

}
