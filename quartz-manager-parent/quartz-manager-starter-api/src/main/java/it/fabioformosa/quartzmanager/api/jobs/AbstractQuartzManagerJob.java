package it.fabioformosa.quartzmanager.api.jobs;

import it.fabioformosa.quartzmanager.api.jobs.entities.LogRecord;
import it.fabioformosa.quartzmanager.api.websockets.WebSocketProgressNotifier;
import it.fabioformosa.quartzmanager.api.websockets.WebhookSender;
import it.fabioformosa.quartzmanager.api.dto.TriggerFiredBundleDTO;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

/**
 * Extends this class to create a job that produces LogRecord to be displayed
 * into the GUI panel
 *
 * @author Fabio.Formosa
 */
public abstract class AbstractQuartzManagerJob implements Job {

  private static final Logger log = LoggerFactory.getLogger(AbstractQuartzManagerJob.class);

  @Resource
  private WebhookSender<TriggerFiredBundleDTO> webSocketProgressNotifier;

  @Resource
  private WebhookSender<LogRecord> webSocketLogsNotifier;

  /**
   * @param jobExecutionContext
   * @return final log
   */
  public abstract LogRecord doIt(JobExecutionContext jobExecutionContext);

  @Override
  public final void execute(JobExecutionContext jobExecutionContext) {
    LogRecord logMsg = doIt(jobExecutionContext);
    log.info(logMsg.getMessage());

    logMsg.setThreadName(Thread.currentThread().getName());
    webSocketLogsNotifier.send(logMsg);

    TriggerFiredBundleDTO triggerFiredBundleDTO = WebSocketProgressNotifier.buildTriggerFiredBundle(jobExecutionContext);
    webSocketProgressNotifier.send(triggerFiredBundleDTO);
  }

}
