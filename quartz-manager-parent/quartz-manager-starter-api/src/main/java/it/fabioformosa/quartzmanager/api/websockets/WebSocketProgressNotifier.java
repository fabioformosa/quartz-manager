package it.fabioformosa.quartzmanager.api.websockets;

import it.fabioformosa.quartzmanager.api.dto.TriggerFiredBundleDTO;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

/**
 * Notify the progress of the trigger through websocket
 *
 * @author Fabio Formosa
 */
@Component
public class WebSocketProgressNotifier implements WebhookSender<TriggerFiredBundleDTO> {

  public static final String TOPIC_PROGRESS = "/topic/progress";

  @Autowired
  private SimpMessageSendingOperations messagingTemplate;

  @Override
  public void send(TriggerFiredBundleDTO triggerFiredBundleDTO) {
     messagingTemplate.convertAndSend(TOPIC_PROGRESS, triggerFiredBundleDTO);
  }

  public static TriggerFiredBundleDTO buildTriggerFiredBundle(JobExecutionContext jobExecutionContext) {
    TriggerFiredBundleDTO triggerFiredBundleDTO = new TriggerFiredBundleDTO();

    Trigger trigger = jobExecutionContext.getTrigger();
    triggerFiredBundleDTO.setFinalFireTime(trigger.getFinalFireTime());
    triggerFiredBundleDTO.setNextFireTime(trigger.getNextFireTime());
    triggerFiredBundleDTO.setPreviousFireTime(trigger.getPreviousFireTime());

    if (trigger instanceof SimpleTrigger) {
      SimpleTrigger simpleTrigger = (SimpleTrigger) trigger;
      triggerFiredBundleDTO.setRepeatCount(simpleTrigger.getRepeatCount() + 1);
      triggerFiredBundleDTO.setTimesTriggered(simpleTrigger.getTimesTriggered());
    } else if (trigger instanceof DailyTimeIntervalTrigger) {
      DailyTimeIntervalTrigger dailyTrigger = (DailyTimeIntervalTrigger) trigger;
      triggerFiredBundleDTO.setRepeatCount(dailyTrigger.getRepeatCount() + 1);
    }

    JobDetail jobDetail = jobExecutionContext.getJobDetail();
    triggerFiredBundleDTO.setJobKey(jobDetail.getKey().getName());
    triggerFiredBundleDTO.setJobClass(jobDetail.getJobClass().getName());
    return triggerFiredBundleDTO;
  }

}
