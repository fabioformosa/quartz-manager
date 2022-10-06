package it.fabioformosa.samplepackage;

import it.fabioformosa.quartzmanager.api.jobs.AbstractQuartzManagerJob;
import it.fabioformosa.quartzmanager.api.jobs.entities.LogRecord;
import it.fabioformosa.quartzmanager.api.jobs.entities.LogRecord.LogType;
import org.quartz.JobExecutionContext;

public class SampleExtraJob extends AbstractQuartzManagerJob {

  @Override
  public LogRecord doIt(JobExecutionContext jobExecutionContext) {
    return new LogRecord(LogType.INFO, "Hello!");
  }

}
