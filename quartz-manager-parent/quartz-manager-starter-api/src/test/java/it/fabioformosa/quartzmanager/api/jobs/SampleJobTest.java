package it.fabioformosa.quartzmanager.api.jobs;

import it.fabioformosa.quartzmanager.api.dto.TriggerFiredBundleDTO;
import it.fabioformosa.quartzmanager.api.jobs.entities.LogRecord;
import it.fabioformosa.quartzmanager.api.websockets.WebhookSender;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.ScheduleBuilder;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class SampleJobTest {

  @InjectMocks
  private SampleJob sampleJob;

  @Mock
  private WebhookSender<TriggerFiredBundleDTO> webSocketProgressNotifier;
  @Mock
  private WebhookSender<LogRecord> webSocketLogsNotifier;

  @Captor
  private ArgumentCaptor<LogRecord> logRecordCaptor;

  @Captor
  private ArgumentCaptor<TriggerFiredBundleDTO> triggerFiredBundleDTOCaptor;

  @Test
  void givenASampleJob_whenTheJobIsExecuted_thenTheWebhookSendersAreCalled() {
    JobExecutionContext jobExecutionContext = Mockito.mock(JobExecutionContext.class);
    String triggerName = "test-trigger";

    ScheduleBuilder<?> schedulerBuilder = SimpleScheduleBuilder.simpleSchedule()
      .withRepeatCount(5)
      .withIntervalInMilliseconds(1000L);
    JobDetail jobDetail = JobBuilder
      .newJob(SampleJob.class).withIdentity(JobKey.jobKey("test-job"))
      .build();
    Trigger trigger = TriggerBuilder.newTrigger()
      .withIdentity(triggerName)
      .forJob(jobDetail)
      .withSchedule(schedulerBuilder)
      .build();
    Mockito.when(jobExecutionContext.getTrigger()).thenReturn(trigger);
    Mockito.when(jobExecutionContext.getJobDetail()).thenReturn(jobDetail);

    sampleJob.execute(jobExecutionContext);
    Mockito.verify(webSocketLogsNotifier).send(eq(triggerName), logRecordCaptor.capture());
    LogRecord actualLogRecord = logRecordCaptor.getValue();
    Assertions.assertThat(actualLogRecord.getMessage()).isEqualTo("Hello!");
    Assertions.assertThat(actualLogRecord.getType()).isEqualTo(LogRecord.LogType.INFO);
    Assertions.assertThat(actualLogRecord.getDate()).isNotNull();
    Assertions.assertThat(actualLogRecord.getThreadName()).isNotNull();

    Mockito.verify(webSocketProgressNotifier).send(eq(triggerName), triggerFiredBundleDTOCaptor.capture());
    TriggerFiredBundleDTO triggerFiredBundleDTO = triggerFiredBundleDTOCaptor.getValue();
    Assertions.assertThat(triggerFiredBundleDTO.getJobKey()).isEqualTo("test-job");
    Assertions.assertThat(triggerFiredBundleDTO.getRepeatCount()).isEqualTo(6);
    Assertions.assertThat(triggerFiredBundleDTO.getJobClass()).isEqualTo(SampleJob.class.getName());
    Assertions.assertThat(triggerFiredBundleDTO.getTimesTriggered()).isZero();
    Assertions.assertThat(triggerFiredBundleDTO.getNextFireTime()).isNull();
    Assertions.assertThat(triggerFiredBundleDTO.getPercentage()).isZero();
    Assertions.assertThat(triggerFiredBundleDTO.getFinalFireTime()).isNotNull();
    Assertions.assertThat(triggerFiredBundleDTO.getPreviousFireTime()).isNull();
  }

  @Test
  void givenASampleJob_whenTheDoItMethodIsCalled_thenALogRecordIsReturned() {
    JobExecutionContext jobExecutionContext = Mockito.mock(JobExecutionContext.class);
    LogRecord logRecord = sampleJob.doIt(jobExecutionContext);
    Assertions.assertThat(logRecord.getMessage()).isEqualTo("Hello!");
    Assertions.assertThat(logRecord.getType()).isEqualTo(LogRecord.LogType.INFO);
    Assertions.assertThat(logRecord.getDate()).isNotNull();
  }

}
