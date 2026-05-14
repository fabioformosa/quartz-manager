package it.fabioformosa.quartzmanager.api.services;

import it.fabioformosa.quartzmanager.api.dto.JobKeyDTO;
import it.fabioformosa.quartzmanager.api.dto.ScheduledJobDTO;
import it.fabioformosa.quartzmanager.api.dto.ScheduledJobInputDTO;
import it.fabioformosa.quartzmanager.api.dto.TriggerKeyDTO;
import it.fabioformosa.quartzmanager.api.exceptions.JobNotFoundException;
import it.fabioformosa.quartzmanager.api.exceptions.ResourceConflictException;
import it.fabioformosa.quartzmanager.api.jobs.AbstractQuartzManagerJob;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobDetail;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.matchers.GroupMatcher;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class JobService {

  @Getter
  private List<Class<? extends AbstractQuartzManagerJob>> jobClasses = new ArrayList<>();

  private List<String> jobClassPackages = new ArrayList<>();
  private final Scheduler scheduler;
  private final ConversionService conversionService;

  public JobService(String jobClassPackages) {
    this(jobClassPackages, null, null);
  }

  @Autowired
  public JobService(@Value("${quartz-manager.jobClassPackages}") String jobClassPackages,
                    @Qualifier("quartzManagerScheduler") Scheduler scheduler,
                    ConversionService conversionService) {
    this.scheduler = scheduler;
    this.conversionService = conversionService;
    List<String> splitPackages = Arrays.stream(Optional.of(jobClassPackages).map(str -> str.split(","))
        .orElseThrow(() -> new RuntimeException("The prop quartz-manager.jobClassPackages  cannot be blank!")))
      .map(String::trim)
      .filter(StringUtils::isNotBlank)
      .collect(Collectors.toList());
    if (!splitPackages.isEmpty())
      this.jobClassPackages.addAll(splitPackages);
  }

  @PostConstruct
  public void initJobClassList() {
    List<Class<? extends AbstractQuartzManagerJob>> foundJobClasses = jobClassPackages.stream().flatMap(jobClassPackage -> findJobClassesInPackage(jobClassPackage).stream()).collect(Collectors.toList());
    if (!foundJobClasses.isEmpty()) {
      log.info("Found the following eligible job classes: {}", foundJobClasses);
      this.jobClasses.addAll(foundJobClasses);
    }
    else
      log.warn("Not found any eligible job classes!");
  }

  private static Set<Class<? extends AbstractQuartzManagerJob>> findJobClassesInPackage(String packageStr) {
    Reflections reflections = new Reflections(packageStr);
    return reflections.getSubTypesOf(AbstractQuartzManagerJob.class);
  }

  public List<ScheduledJobDTO> fetchScheduledJobs() throws SchedulerException {
    Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.anyJobGroup());
    return jobKeys.stream().map(this::convertJob).toList();
  }

  public ScheduledJobDTO getScheduledJob(String group, String name) throws SchedulerException, JobNotFoundException {
    JobKey jobKey = JobKey.jobKey(name, group);
    if (!scheduler.checkExists(jobKey))
      throw new JobNotFoundException(group, name);
    return convertJob(jobKey);
  }

  public ScheduledJobDTO createJob(String group, String name, ScheduledJobInputDTO scheduledJobInputDTO) throws SchedulerException, ClassNotFoundException {
    JobKey jobKey = JobKey.jobKey(name, group);
    if (scheduler.checkExists(jobKey))
      throw new ResourceConflictException("Job " + jobKey + " already exists");

    JobDetail jobDetail = buildJobDetail(jobKey, scheduledJobInputDTO);
    scheduler.addJob(jobDetail, false);
    return convertJob(jobKey);
  }

  public ScheduledJobDTO updateJob(String group, String name, ScheduledJobInputDTO scheduledJobInputDTO) throws SchedulerException, ClassNotFoundException, JobNotFoundException {
    JobKey jobKey = requireJob(group, name);
    JobDetail jobDetail = buildJobDetail(jobKey, scheduledJobInputDTO);
    scheduler.addJob(jobDetail, true);
    return convertJob(jobKey);
  }

  public void triggerJob(String group, String name) throws SchedulerException, JobNotFoundException {
    JobKey jobKey = requireJob(group, name);
    scheduler.triggerJob(jobKey);
  }

  public void deleteJob(String group, String name) throws SchedulerException, JobNotFoundException {
    JobKey jobKey = requireJob(group, name);
    scheduler.deleteJob(jobKey);
  }

  public Class<? extends Job> getEligibleJobClass(String jobClassName) throws ClassNotFoundException {
    return jobClasses.stream()
      .filter(jobClass -> jobClass.getName().equals(jobClassName))
      .findFirst()
      .orElseThrow(() -> new ClassNotFoundException("Job class " + jobClassName + " is not eligible"));
  }

  private JobKey requireJob(String group, String name) throws SchedulerException, JobNotFoundException {
    JobKey jobKey = JobKey.jobKey(name, group);
    if (!scheduler.checkExists(jobKey))
      throw new JobNotFoundException(group, name);
    return jobKey;
  }

  private JobDetail buildJobDetail(JobKey jobKey, ScheduledJobInputDTO scheduledJobInputDTO) throws ClassNotFoundException {
    Class<? extends Job> jobClass = getEligibleJobClass(scheduledJobInputDTO.getJobClass());
    JobBuilder jobBuilder = JobBuilder.newJob(jobClass)
      .withIdentity(jobKey)
      .storeDurably(scheduledJobInputDTO.isDurable())
      .requestRecovery(scheduledJobInputDTO.isRequestsRecovery());
    if (scheduledJobInputDTO.getDescription() != null)
      jobBuilder.withDescription(scheduledJobInputDTO.getDescription());
    if (scheduledJobInputDTO.getJobDataMap() != null)
      jobBuilder.usingJobData(new JobDataMap(scheduledJobInputDTO.getJobDataMap()));
    return jobBuilder.build();
  }

  private ScheduledJobDTO convertJob(JobKey jobKey) {
    try {
      JobDetail jobDetail = scheduler.getJobDetail(jobKey);
      List<TriggerKeyDTO> triggerKeys = scheduler.getTriggersOfJob(jobKey).stream()
        .map(Trigger::getKey)
        .map(triggerKey -> conversionService.convert(triggerKey, TriggerKeyDTO.class))
        .toList();
      return ScheduledJobDTO.builder()
        .jobKeyDTO(conversionService.convert(jobKey, JobKeyDTO.class))
        .jobClassName(jobDetail.getJobClass().getName())
        .description(jobDetail.getDescription())
        .durable(jobDetail.isDurable())
        .requestsRecovery(jobDetail.requestsRecovery())
        .jobDataMap(jobDetail.getJobDataMap())
        .triggerKeys(triggerKeys)
        .build();
    } catch (SchedulerException ex) {
      throw new IllegalStateException("Unable to read job " + jobKey, ex);
    }
  }

}
