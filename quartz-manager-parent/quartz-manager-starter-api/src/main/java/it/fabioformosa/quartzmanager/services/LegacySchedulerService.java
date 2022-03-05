package it.fabioformosa.quartzmanager.services;

import it.fabioformosa.quartzmanager.common.utils.Try;
import it.fabioformosa.quartzmanager.dto.SchedulerConfigParam;
import it.fabioformosa.quartzmanager.dto.TriggerDTO;
import it.fabioformosa.quartzmanager.exceptions.TriggerNotFoundException;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.quartz.TriggerBuilder.newTrigger;

@Service
public class LegacySchedulerService extends AbstractSchedulerService {

  public static final int MILLS_IN_A_DAY = 1000 * 60 * 60 * 24;
  public static final int SEC_IN_A_DAY = 60 * 60 * 24;

  public LegacySchedulerService(Scheduler scheduler, ConversionService conversionService) {
    super(scheduler, conversionService);
  }

  public static int fromTriggerPerDayToMillsInterval(long triggerPerDay) {
    return (int) Math.ceil(Long.valueOf(LegacySchedulerService.MILLS_IN_A_DAY) / triggerPerDay); // with ceil the triggerPerDay is a max value
  }

  public static int fromTriggerPerDayToSecInterval(long triggerPerDay) {
    return (int) Math.ceil(Long.valueOf(LegacySchedulerService.SEC_IN_A_DAY) / triggerPerDay);
  }

  public static long fromMillsIntervalToTriggerPerDay(long repeatIntervalInMills) {
    return (int) Math.ceil(MILLS_IN_A_DAY / repeatIntervalInMills);
  }

  public Scheduler getScheduler() {
    return scheduler;
  }

  public Optional<TriggerKey> getTriggerByKey(String triggerKeyName) throws SchedulerException {
    return scheduler.getTriggerKeys(GroupMatcher.anyGroup()).stream()
      .filter(triggerKey -> triggerKey.getName().equals(triggerKeyName))
      .findFirst();
  }

  public Optional<SimpleTrigger> getOneSimpleTrigger() throws SchedulerException {
    return getOneTriggerKey()
      .map(Try.with(triggerKey -> scheduler.getTrigger(triggerKey)))
      .filter(Try::isSuccess).map(Try::getSuccess)
      .filter(trigger -> trigger instanceof SimpleTrigger)
      .map(trigger -> (SimpleTrigger) trigger);
  }

  public Optional<TriggerKey> getOneTriggerKey() throws SchedulerException {
    return scheduler.getTriggerKeys(GroupMatcher.anyGroup()).stream()
      .findFirst();
  }

  public TriggerDTO getLegacyTriggerByName(String name) throws SchedulerException, TriggerNotFoundException {
    Trigger trigger = getTriggerByName(name);
    return conversionService.convert(trigger, TriggerDTO.class);
  }



  public TriggerDTO scheduleNewTrigger(String name, String jobClassname, SchedulerConfigParam config) throws SchedulerException, ClassNotFoundException {
    Class<? extends Job> jobClass = (Class<? extends Job>) Class.forName(jobClassname);
    JobDetail jobDetail = JobBuilder.newJob()
      .ofType(jobClass)
      .storeDurably(false)
      .build();

    int intervalInMills = LegacySchedulerService.fromTriggerPerDayToMillsInterval(config.getTriggerPerDay());

    Trigger newTrigger = newTrigger()
      .withSchedule(
        SimpleScheduleBuilder.simpleSchedule()
          .withIntervalInMilliseconds(intervalInMills)
          .withRepeatCount(config.getMaxCount() - 1)
          .withMisfireHandlingInstructionNextWithRemainingCount()
      )
      .withIdentity(name)
      .build();

    scheduler.scheduleJob(jobDetail, newTrigger);

    return conversionService.convert(newTrigger, TriggerDTO.class);
  }



  public TriggerDTO rescheduleTrigger(String name, SchedulerConfigParam config) throws SchedulerException {
    int intervalInMills = LegacySchedulerService.fromTriggerPerDayToMillsInterval(config.getTriggerPerDay());

    Optional<TriggerKey> optionalTriggerKey = getTriggerByKey(name);
    TriggerKey triggerKey = optionalTriggerKey.orElse(TriggerKey.triggerKey(name));
    Trigger trigger = scheduler.getTrigger(triggerKey);

    Trigger newTrigger = newTrigger()
      .withSchedule(
        SimpleScheduleBuilder.simpleSchedule()
          .withIntervalInMilliseconds(intervalInMills)
          .withRepeatCount(config.getMaxCount() - 1)
          .withMisfireHandlingInstructionNextWithRemainingCount()
      )
      .forJob(trigger.getJobKey().getName())
      .withIdentity(name)
      .build();

    scheduler.rescheduleJob(triggerKey, newTrigger);

    return conversionService.convert(newTrigger, TriggerDTO.class);
  }

}
