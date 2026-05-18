package it.fabioformosa.quartzmanager.api.services;

import it.fabioformosa.quartzmanager.api.dto.CurrentExecutionDTO;
import it.fabioformosa.quartzmanager.api.dto.JobKeyDTO;
import it.fabioformosa.quartzmanager.api.dto.TriggerKeyDTO;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class ExecutionService {

  private final Scheduler scheduler;
  private final ConversionService conversionService;

  public ExecutionService(@Qualifier("quartzManagerScheduler") Scheduler scheduler, ConversionService conversionService) {
    this.scheduler = scheduler;
    this.conversionService = conversionService;
  }

  public List<CurrentExecutionDTO> getCurrentExecutions() throws SchedulerException {
    String node = scheduler.getSchedulerInstanceId();
    return scheduler.getCurrentlyExecutingJobs().stream()
      .map(executionContext -> toCurrentExecutionDTO(executionContext, node))
      .toList();
  }

  public List<CurrentExecutionDTO> getRecoveringExecutions() throws SchedulerException {
    return getCurrentExecutions().stream()
      .filter(CurrentExecutionDTO::isRecovering)
      .toList();
  }

  private CurrentExecutionDTO toCurrentExecutionDTO(JobExecutionContext executionContext, String node) {
    long runTime = executionContext.getJobRunTime();
    if (runTime < 0 && executionContext.getFireTime() != null)
      runTime = Math.max(0, Instant.now().toEpochMilli() - executionContext.getFireTime().getTime());

    return CurrentExecutionDTO.builder()
      .fireInstanceId(executionContext.getFireInstanceId())
      .jobKeyDTO(conversionService.convert(executionContext.getJobDetail().getKey(), JobKeyDTO.class))
      .triggerKeyDTO(conversionService.convert(executionContext.getTrigger().getKey(), TriggerKeyDTO.class))
      .fireTime(executionContext.getFireTime())
      .scheduledFireTime(executionContext.getScheduledFireTime())
      .previousFireTime(executionContext.getPreviousFireTime())
      .nextFireTime(executionContext.getNextFireTime())
      .runTime(runTime)
      .refireCount(executionContext.getRefireCount())
      .recovering(executionContext.isRecovering())
      .node(node)
      .build();
  }
}
