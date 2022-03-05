package it.fabioformosa.quartzmanager.jobs;

import it.fabioformosa.quartzmanager.aspects.ProgressNotifier;
import it.fabioformosa.quartzmanager.jobs.entities.LogRecord;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

import javax.annotation.Resource;

/**
 * Extends this class to create a job that produces LogRecord to be displayed
 * into the GUI panel
 *
 * @author Fabio.Formosa
 *
 */
public abstract class AbstractLoggingJob implements Job {

  private static final Logger log = LoggerFactory.getLogger(AbstractLoggingJob.class);

  @Autowired
  private SimpMessageSendingOperations messagingTemplate;

  @Resource
  private ProgressNotifier progressNotifier;

  /**
   *
   * @param jobExecutionContext
   * @return final log
   */
  public abstract LogRecord doIt(JobExecutionContext jobExecutionContext);

  @Override
  public final void execute(JobExecutionContext jobExecutionContext) {
    try {
      LogRecord logMsg = doIt(jobExecutionContext);
      logAndSend(logMsg);
      progressNotifier.send(jobExecutionContext);
    } catch (SchedulerException e) {
      log.error("Error updating progress " + e.getMessage());
    }
  }

  public void logAndSend(LogRecord logRecord) {
    log.info(logRecord.getMessage());
    logRecord.setThreadName(Thread.currentThread().getName());
    messagingTemplate.convertAndSend("/topic/logs", logRecord);
  }

}
