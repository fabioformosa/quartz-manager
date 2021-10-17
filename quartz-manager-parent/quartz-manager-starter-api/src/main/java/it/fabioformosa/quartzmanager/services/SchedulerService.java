package it.fabioformosa.quartzmanager.services;

import it.fabioformosa.quartzmanager.common.utils.Try;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SchedulerService {

  public static final int MILLS_IN_A_DAY = 1000 * 60 * 60 * 24;
  public static final int SEC_IN_A_DAY = 60 * 60 * 24;

  private Scheduler scheduler;

  public SchedulerService(Scheduler scheduler) {
    this.scheduler = scheduler;
  }

  public static int fromTriggerPerDayToMillsInterval(long triggerPerDay) {
    return (int) Math.ceil(Long.valueOf(SchedulerService.MILLS_IN_A_DAY) / triggerPerDay); // with ceil the triggerPerDay is a max value
  }

  public static int fromTriggerPerDayToSecInterval(long triggerPerDay) {
    return (int) Math.ceil(Long.valueOf(SchedulerService.SEC_IN_A_DAY) / triggerPerDay);
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

}
