package it.fabioformosa.quartzmanager.aspects;

import it.fabioformosa.quartzmanager.dto.TriggerStatus;
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
        TriggerStatus currTriggerStatus = new TriggerStatus();

        Trigger trigger = jobExecutionContext.getTrigger();
        currTriggerStatus.setFinalFireTime(trigger.getFinalFireTime());
        currTriggerStatus.setNextFireTime(trigger.getNextFireTime());
        currTriggerStatus.setPreviousFireTime(trigger.getPreviousFireTime());

        if (trigger instanceof SimpleTrigger) {
            SimpleTrigger simpleTrigger = (SimpleTrigger) trigger;
            currTriggerStatus.setRepeatCount(simpleTrigger.getRepeatCount() + 1);
            currTriggerStatus.setTimesTriggered(simpleTrigger.getTimesTriggered());
        } else if (trigger instanceof DailyTimeIntervalTrigger) {
            DailyTimeIntervalTrigger dailyTrigger = (DailyTimeIntervalTrigger) trigger;
            currTriggerStatus.setRepeatCount(dailyTrigger.getRepeatCount() + 1);
        }

        JobDetail jobDetail = jobExecutionContext.getJobDetail();
        currTriggerStatus.setJobKey(jobDetail.getKey().getName());
        currTriggerStatus.setJobClass(trigger.getClass().getSimpleName());

        messagingTemplate.convertAndSend("/topic/progress", currTriggerStatus);
    }

}
