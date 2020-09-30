package it.fabioformosa.quartzmanager.aspects;

import javax.annotation.Resource;

import org.quartz.DailyTimeIntervalTrigger;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

import it.fabioformosa.quartzmanager.dto.TriggerStatus;
import it.fabioformosa.quartzmanager.scheduler.TriggerMonitor;

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

	@Resource
	private Scheduler scheduler;

	@Resource
	private TriggerMonitor triggerMonitor;

	//@AfterReturning("execution(* logAndSend(..))")
	//	@Override
	//	public void updateProgress(JoinPoint joinPoint) {
	//		log.info("PROGRESS UPDATE!!!");
	//	}

	@Override
	public void send() throws SchedulerException {
		TriggerStatus currTriggerStatus = new TriggerStatus();

		Trigger trigger = scheduler.getTrigger(triggerMonitor.getTrigger().getKey());
		currTriggerStatus.setFinalFireTime(trigger.getFinalFireTime());
		currTriggerStatus.setNextFireTime(trigger.getNextFireTime());
		currTriggerStatus.setPreviousFireTime(trigger.getPreviousFireTime());

		int timesTriggered = 0;
		int repeatCount = 0;

		if (trigger instanceof SimpleTrigger) {
			SimpleTrigger simpleTrigger = (SimpleTrigger) trigger;
			timesTriggered = simpleTrigger.getTimesTriggered();
			repeatCount = simpleTrigger.getRepeatCount();
		} else if (trigger instanceof DailyTimeIntervalTrigger) {
			DailyTimeIntervalTrigger dailyTrigger = (DailyTimeIntervalTrigger) trigger;
			timesTriggered = dailyTrigger.getTimesTriggered();
			repeatCount = dailyTrigger.getRepeatCount();
		}

		Trigger jobTrigger = triggerMonitor.getTrigger();
		if (jobTrigger != null && jobTrigger.getJobKey() != null) {
			currTriggerStatus.setJobKey(jobTrigger.getJobKey().getName());
			currTriggerStatus.setJobClass(jobTrigger.getClass().getSimpleName());
			currTriggerStatus.setTimesTriggered(timesTriggered);
			currTriggerStatus.setRepeatCount(repeatCount + 1);
		}

		messagingTemplate.convertAndSend("/topic/progress", currTriggerStatus);
	}

}
