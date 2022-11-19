package it.fabioformosa.quartzmanager.jobs.tests;

import it.fabioformosa.quartzmanager.api.jobs.AbstractQuartzManagerJob;
import it.fabioformosa.quartzmanager.api.jobs.entities.LogRecord;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This job can be used to test the misfire policy. It pretends to be a long
 * processing job (sleeping for a while)
 *
 * @author Fabio.Formosa
 *
 */
@Slf4j
public class MisfireTestJob extends AbstractQuartzManagerJob {

  private Logger log = LoggerFactory.getLogger(MisfireTestJob.class);

  @Override
  public LogRecord doIt(JobExecutionContext jobExecutionContext) {
    try {
      log.info("{} is going to sleep...", Thread.currentThread().getName());

      Thread.sleep(10 * 1000L);

      log.info("{} woke up!", Thread.currentThread().getName());
    } catch (InterruptedException e) {
      throw new IllegalStateException(e);
    }

    return new LogRecord(LogRecord.LogType.INFO, "Hello!");
  }

}
