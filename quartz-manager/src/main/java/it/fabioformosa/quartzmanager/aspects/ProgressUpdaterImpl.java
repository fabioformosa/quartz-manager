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

import it.fabioformosa.quartzmanager.dto.TriggerProgress;
import it.fabioformosa.quartzmanager.scheduler.TriggerMonitor;

//@Aspect
@Component
public class ProgressUpdaterImpl implements ProgressUpdater {

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
	public void update() throws SchedulerException {
		Trigger trigger = scheduler.getTrigger(triggerMonitor.getTrigger().getKey());

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

		TriggerProgress progress = new TriggerProgress();
		Trigger jobTrigger = triggerMonitor.getTrigger();
		if (jobTrigger != null && jobTrigger.getJobKey() != null) {
			progress.setJobKey(jobTrigger.getJobKey().getName());
			progress.setJobClass(jobTrigger.getClass().getSimpleName());
			progress.setTimesTriggered(timesTriggered);
			progress.setRepeatCount(repeatCount + 1);
			progress.setFinalFireTime(jobTrigger.getFinalFireTime());
			progress.setNextFireTime(jobTrigger.getNextFireTime());
			progress.setPreviousFireTime(jobTrigger.getPreviousFireTime());
		}

		messagingTemplate.convertAndSend("/topic/progress", progress);
	}

}
