package it.fabioformosa.quartzmanager.aspects;

import it.fabioformosa.quartzmanager.dto.TriggerFiredBundleDTO;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

/**
 *
 * Notify the progress of the trigger through websocket
 *
 * @author Fabio Formosa
 *
 */
//@Aspect
@Component
public class WebSocketProgressNotifier implements ProgressNotifier {

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    //@AfterReturning("execution(* logAndSend(..))")
    //	@Override
    //	public void updateProgress(JoinPoint joinPoint) {
    //		log.info("PROGRESS UPDATE!!!");
    //	}

    @Override
    public void send(JobExecutionContext jobExecutionContext) throws SchedulerException {
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
        triggerFiredBundleDTO.setJobClass(trigger.getClass().getSimpleName());

        messagingTemplate.convertAndSend("/topic/progress", triggerFiredBundleDTO);
    }

}
