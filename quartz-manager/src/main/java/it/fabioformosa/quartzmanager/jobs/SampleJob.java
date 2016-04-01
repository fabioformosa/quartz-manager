package it.fabioformosa.quartzmanager.jobs;

import org.quartz.JobExecutionContext;

public class SampleJob extends AbstractLoggingJob {

	@Override
	public String doIt(JobExecutionContext jobExecutionContext) {
		return "Hello!";
	}

}
