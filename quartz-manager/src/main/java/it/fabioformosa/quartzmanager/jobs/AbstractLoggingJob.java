package it.fabioformosa.quartzmanager.jobs;

import javax.annotation.Resource;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

import it.fabioformosa.quartzmanager.aspects.ProgressUpdater;

public abstract class AbstractLoggingJob implements Job {

	private static final Logger log = LoggerFactory
			.getLogger(AbstractLoggingJob.class);

	@Autowired
	private SimpMessageSendingOperations messagingTemplate;

	@Resource
	private ProgressUpdater progressUpdater;

	/**
	 *
	 * @param jobExecutionContext
	 * @return final log
	 */
	public abstract String doIt(JobExecutionContext jobExecutionContext);

	@Override
	public final void execute(JobExecutionContext jobExecutionContext) {
		try {
			String logMsg = doIt(jobExecutionContext);
			logAndSend(logMsg);
			progressUpdater.update();
		} catch (SchedulerException e) {
			log.error("Error updating progress " + e.getMessage());
		}
	}

	public void logAndSend(String logMsg) {
		log.info(logMsg);
		messagingTemplate.convertAndSend("/topic/logs", logMsg);
	}

}