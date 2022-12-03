package it.fabioformosa.quartzmanager.jobs.tests;

import it.fabioformosa.quartzmanager.api.jobs.AbstractQuartzManagerJob;
import it.fabioformosa.quartzmanager.api.jobs.entities.LogRecord;
import lombok.Generated;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;

/**
 * This job can be used to test the misfire policy. It pretends to be a long
 * processing job (sleeping for a while)
 *
 * @author Fabio.Formosa
 *
 */
@Slf4j
@NoArgsConstructor
@Generated
public class MisfireTestJob extends AbstractQuartzManagerJob {

  private long sleepPeriodInMs = 10 * 1000L;

  public MisfireTestJob(long sleepPeriodInMs) {
    this.sleepPeriodInMs = sleepPeriodInMs;
  }

  @Override
  public LogRecord doIt(JobExecutionContext jobExecutionContext) {
    try {
      log.info("{} is going to sleep...", Thread.currentThread().getName());

      Thread.sleep(sleepPeriodInMs);

      log.info("{} woke up!", Thread.currentThread().getName());
    } catch (InterruptedException e) {
      throw new IllegalStateException(e);
    }

    return new LogRecord(LogRecord.LogType.INFO, "Hello!");
  }

}
