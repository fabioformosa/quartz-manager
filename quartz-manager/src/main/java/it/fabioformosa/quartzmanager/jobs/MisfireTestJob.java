package it.fabioformosa.quartzmanager.jobs;

import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.fabioformosa.quartzmanager.jobs.entities.LogRecord;
import it.fabioformosa.quartzmanager.jobs.entities.LogRecord.LogType;

public class MisfireTestJob extends AbstractLoggingJob {

	private Logger log = LoggerFactory.getLogger(MisfireTestJob.class);

	@Override
	public LogRecord doIt(JobExecutionContext jobExecutionContext) {
		try {
			log.info("{} is going to sleep...", Thread.currentThread().getName());
			Thread.sleep(10 * 1000);
			log.info("{} woke up!", Thread.currentThread().getName());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return new LogRecord(LogType.INFO, "Hello!");
	}

}
