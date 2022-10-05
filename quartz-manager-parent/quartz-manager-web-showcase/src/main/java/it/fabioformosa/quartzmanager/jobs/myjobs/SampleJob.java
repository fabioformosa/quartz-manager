package it.fabioformosa.quartzmanager.jobs.myjobs;

import org.quartz.JobExecutionContext;

import it.fabioformosa.quartzmanager.jobs.AbstractQuartzManagerJob;
import it.fabioformosa.quartzmanager.jobs.entities.LogRecord;
import it.fabioformosa.quartzmanager.jobs.entities.LogRecord.LogType;


public class SampleJob extends AbstractQuartzManagerJob {
    @Override
    public LogRecord doIt(JobExecutionContext jobExecutionContext) {
      return new LogRecord(LogType.INFO, "Hello World!");
    }

}
