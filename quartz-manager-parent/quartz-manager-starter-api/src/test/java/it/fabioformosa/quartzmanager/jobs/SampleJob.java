package it.fabioformosa.quartzmanager.jobs;

import it.fabioformosa.quartzmanager.jobs.entities.LogRecord;
import it.fabioformosa.quartzmanager.jobs.entities.LogRecord.LogType;
import org.quartz.JobExecutionContext;

public class SampleJob extends AbstractQuartzManagerJob {

  @Override
  public LogRecord doIt(JobExecutionContext jobExecutionContext) {
    return new LogRecord(LogType.INFO, "Hello!");
  }

}
