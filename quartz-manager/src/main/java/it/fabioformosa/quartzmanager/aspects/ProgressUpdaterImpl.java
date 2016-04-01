package it.fabioformosa.quartzmanager.aspects;

import javax.annotation.Resource;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.impl.triggers.SimpleTriggerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

import it.fabioformosa.quartzmanager.dto.TriggerProgress;

//@Aspect
@Component
public class ProgressUpdaterImpl implements ProgressUpdater {

	private static final Logger log = LoggerFactory
			.getLogger(ProgressUpdaterImpl.class);

	@Autowired
	private SimpMessageSendingOperations messagingTemplate;

	@Resource
	private Scheduler scheduler;

	@Resource
	private SimpleTrigger jobTrigger = null;

	//@AfterReturning("execution(* logAndSend(..))")
	//	@Override
	//	public void updateProgress(JoinPoint joinPoint) {
	//		log.info("PROGRESS UPDATE!!!");
	//	}

	@Override
	public void update() throws SchedulerException {
		SimpleTriggerImpl jobTrigger = (SimpleTriggerImpl) scheduler
				.getTrigger(this.jobTrigger.getKey());

		TriggerProgress progress = new TriggerProgress();
		if (jobTrigger != null && jobTrigger.getJobKey() != null) {
			progress.setJobKey(jobTrigger.getJobKey().getName());
			progress.setJobClass(jobTrigger.getClass().getSimpleName());
			progress.setTimesTriggered(jobTrigger.getTimesTriggered());
			progress.setRepeatCount(jobTrigger.getRepeatCount());
			progress.setFinalFireTime(jobTrigger.getFinalFireTime());
			progress.setNextFireTime(jobTrigger.getNextFireTime());
			progress.setPreviousFireTime(jobTrigger.getPreviousFireTime());
		}

		messagingTemplate.convertAndSend("/topic/progress", progress);
	}

}
