package it.fabioformosa.samplepackage;

import it.fabioformosa.quartzmanager.jobs.AbstractQuartzManagerJob;
import it.fabioformosa.quartzmanager.jobs.entities.LogRecord;
import it.fabioformosa.quartzmanager.jobs.entities.LogRecord.LogType;
import org.quartz.JobExecutionContext;

public class SampleExtraJob extends AbstractQuartzManagerJob {

  @Override
  public LogRecord doIt(JobExecutionContext jobExecutionContext) {
    return new LogRecord(LogType.INFO, "Hello!");
  }

}
