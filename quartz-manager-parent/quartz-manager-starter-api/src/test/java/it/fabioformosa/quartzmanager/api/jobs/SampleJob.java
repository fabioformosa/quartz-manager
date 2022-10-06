package it.fabioformosa.quartzmanager.api.jobs;

import it.fabioformosa.quartzmanager.api.jobs.entities.LogRecord;
import org.quartz.JobExecutionContext;

public class SampleJob extends AbstractQuartzManagerJob {

  @Override
  public LogRecord doIt(JobExecutionContext jobExecutionContext) {
    return new LogRecord(LogRecord.LogType.INFO, "Hello!");
  }

}
