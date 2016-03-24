package it.fabioformosa.quartzmanager.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SampleJob implements Job {

	private final Logger log = LoggerFactory.getLogger(SampleJob.class);

	@Override
	public void execute(JobExecutionContext jobExecutionContext) {
		log.info("Hello!");
	}
}
