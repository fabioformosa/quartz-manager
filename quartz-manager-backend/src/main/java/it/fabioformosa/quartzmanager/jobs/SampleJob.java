package it.fabioformosa.quartzmanager.jobs;

import org.quartz.JobExecutionContext;

import it.fabioformosa.quartzmanager.jobs.entities.LogRecord;
import it.fabioformosa.quartzmanager.jobs.entities.LogRecord.LogType;

public class SampleJob extends AbstractLoggingJob {

	@Override
	public LogRecord doIt(JobExecutionContext jobExecutionContext) {
		return new LogRecord(LogType.INFO, "Hello!");
	}

}
