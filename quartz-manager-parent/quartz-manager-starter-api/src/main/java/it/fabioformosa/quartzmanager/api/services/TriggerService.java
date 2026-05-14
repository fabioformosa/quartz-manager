package it.fabioformosa.quartzmanager.api.services;

import it.fabioformosa.quartzmanager.api.dto.JobKeyDTO;
import it.fabioformosa.quartzmanager.api.dto.MisfireInstruction;
import it.fabioformosa.quartzmanager.api.dto.TriggerDTO;
import it.fabioformosa.quartzmanager.api.dto.TriggerInputDTO;
import it.fabioformosa.quartzmanager.api.dto.TriggerKeyDTO;
import it.fabioformosa.quartzmanager.api.dto.TriggerType;
import it.fabioformosa.quartzmanager.api.exceptions.ResourceConflictException;
import it.fabioformosa.quartzmanager.api.exceptions.TriggerNotFoundException;
import org.quartz.CalendarIntervalScheduleBuilder;
import org.quartz.CalendarIntervalTrigger;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.DailyTimeIntervalScheduleBuilder;
import org.quartz.DailyTimeIntervalTrigger;
import org.quartz.DateBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.ScheduleBuilder;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.TimeOfDay;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

@Service
public class TriggerService {

  private static final int DEFAULT_PRIORITY = Trigger.DEFAULT_PRIORITY;
  private static final String MISFIRE_DO_NOTHING = "DO_NOTHING";
  private static final String MISFIRE_IGNORE_MISFIRES = "IGNORE_MISFIRES";

  private final Scheduler scheduler;
  private final ConversionService conversionService;
  private final JobService jobService;

  public TriggerService(@Qualifier("quartzManagerScheduler") Scheduler scheduler, ConversionService conversionService, JobService jobService) {
    this.scheduler = scheduler;
    this.conversionService = conversionService;
    this.jobService = jobService;
  }

  public List<TriggerKeyDTO> fetchTriggers() throws SchedulerException {
    Set<TriggerKey> triggerKeys = scheduler.getTriggerKeys(GroupMatcher.anyTriggerGroup());
    return triggerKeys.stream()
      .map(triggerKey -> conversionService.convert(triggerKey, TriggerKeyDTO.class))
      .toList();
  }

  public TriggerDTO getTrigger(String group, String name) throws SchedulerException, TriggerNotFoundException {
    TriggerKey triggerKey = TriggerKey.triggerKey(name, group);
    Trigger trigger = scheduler.getTrigger(triggerKey);
    if (trigger == null)
      throw new TriggerNotFoundException(group, name);
    TriggerDTO triggerDTO = convertTrigger(trigger);
    triggerDTO.setState(scheduler.getTriggerState(triggerKey).name());
    return triggerDTO;
  }

  public TriggerDTO scheduleTrigger(String group, String name, TriggerInputDTO triggerInputDTO) throws SchedulerException, ClassNotFoundException, ParseException {
    TriggerKey triggerKey = TriggerKey.triggerKey(name, group);
    if (scheduler.checkExists(triggerKey))
      throw new ResourceConflictException("Trigger " + triggerKey + " already exists");

    Trigger newTrigger = buildTrigger(group, name, triggerInputDTO);
    JobKey jobKey = getJobKey(triggerInputDTO);
    if (jobKey != null) {
      if (!scheduler.checkExists(jobKey))
        throw new ResourceConflictException("Job " + jobKey + " does not exist");
      scheduler.scheduleJob(newTrigger.getTriggerBuilder().forJob(jobKey).build());
    }
    else {
      JobDetail jobDetail = JobBuilder.newJob()
        .ofType(jobService.getEligibleJobClass(triggerInputDTO.getJobClass()))
        .storeDurably(false)
        .build();
      scheduler.scheduleJob(jobDetail, newTrigger);
    }

    return convertTrigger(newTrigger);
  }

  public TriggerDTO rescheduleTrigger(String group, String name, TriggerInputDTO triggerInputDTO) throws SchedulerException, TriggerNotFoundException, ParseException {
    TriggerKey triggerKey = TriggerKey.triggerKey(name, group);
    Trigger existingTrigger = scheduler.getTrigger(triggerKey);
    if (existingTrigger == null)
      throw new TriggerNotFoundException(group, name);

    Trigger newTrigger = buildTrigger(group, name, triggerInputDTO).getTriggerBuilder()
      .forJob(existingTrigger.getJobKey())
      .build();
    scheduler.rescheduleJob(triggerKey, newTrigger);
    return convertTrigger(newTrigger);
  }

  public void pauseTrigger(String group, String name) throws SchedulerException, TriggerNotFoundException {
    TriggerKey triggerKey = requireTrigger(group, name);
    scheduler.pauseTrigger(triggerKey);
  }

  public void resumeTrigger(String group, String name) throws SchedulerException, TriggerNotFoundException {
    TriggerKey triggerKey = requireTrigger(group, name);
    scheduler.resumeTrigger(triggerKey);
  }

  public void unscheduleTrigger(String group, String name) throws SchedulerException, TriggerNotFoundException {
    TriggerKey triggerKey = requireTrigger(group, name);
    scheduler.unscheduleJob(triggerKey);
  }

  private TriggerKey requireTrigger(String group, String name) throws SchedulerException, TriggerNotFoundException {
    TriggerKey triggerKey = TriggerKey.triggerKey(name, group);
    if (!scheduler.checkExists(triggerKey))
      throw new TriggerNotFoundException(group, name);
    return triggerKey;
  }

  private Trigger buildTrigger(String group, String name, TriggerInputDTO triggerInputDTO) throws ParseException {
    TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger()
      .withIdentity(name, group)
      .withPriority(triggerInputDTO.getPriority() == null ? DEFAULT_PRIORITY : triggerInputDTO.getPriority());

    if (triggerInputDTO.getStartDate() != null)
      triggerBuilder.startAt(triggerInputDTO.getStartDate());
    if (triggerInputDTO.getEndDate() != null)
      triggerBuilder.endAt(triggerInputDTO.getEndDate());
    if (triggerInputDTO.getDescription() != null)
      triggerBuilder.withDescription(triggerInputDTO.getDescription());
    if (triggerInputDTO.getCalendarName() != null && !triggerInputDTO.getCalendarName().isBlank())
      triggerBuilder.modifiedByCalendar(triggerInputDTO.getCalendarName());
    if (triggerInputDTO.getJobDataMap() != null)
      triggerBuilder.usingJobData(new JobDataMap(triggerInputDTO.getJobDataMap()));

    return triggerBuilder.withSchedule(buildSchedule(triggerInputDTO)).build();
  }

  private ScheduleBuilder<?> buildSchedule(TriggerInputDTO triggerInputDTO) throws ParseException {
    TriggerType triggerType = triggerInputDTO.getTriggerType() == null ? TriggerType.SIMPLE : triggerInputDTO.getTriggerType();
    return switch (triggerType) {
      case SIMPLE -> buildSimpleSchedule(triggerInputDTO);
      case CRON -> buildCronSchedule(triggerInputDTO);
      case DAILY_TIME_INTERVAL -> buildDailyTimeIntervalSchedule(triggerInputDTO);
      case CALENDAR_INTERVAL -> buildCalendarIntervalSchedule(triggerInputDTO);
    };
  }

  private SimpleScheduleBuilder buildSimpleSchedule(TriggerInputDTO triggerInputDTO) {
    SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule();
    if (triggerInputDTO.getRepeatInterval() != null)
      scheduleBuilder.withIntervalInMilliseconds(triggerInputDTO.getRepeatInterval());
    if (triggerInputDTO.getRepeatCount() != null)
      scheduleBuilder.withRepeatCount(triggerInputDTO.getRepeatCount());

    MisfireInstruction misfireInstruction = parseSimpleMisfireInstruction(triggerInputDTO.getMisfireInstruction());
    switch (misfireInstruction) {
      case MISFIRE_INSTRUCTION_FIRE_NOW -> scheduleBuilder.withMisfireHandlingInstructionFireNow();
      case MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT -> scheduleBuilder.withMisfireHandlingInstructionNowWithExistingCount();
      case MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_REMAINING_REPEAT_COUNT -> scheduleBuilder.withMisfireHandlingInstructionNowWithRemainingCount();
      case MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT -> scheduleBuilder.withMisfireHandlingInstructionNextWithRemainingCount();
      case MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT -> scheduleBuilder.withMisfireHandlingInstructionNextWithExistingCount();
    }
    return scheduleBuilder;
  }

  private CronScheduleBuilder buildCronSchedule(TriggerInputDTO triggerInputDTO) throws ParseException {
    CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronScheduleNonvalidatedExpression(triggerInputDTO.getCronExpression());
    if (triggerInputDTO.getTimeZone() != null && !triggerInputDTO.getTimeZone().isBlank())
      scheduleBuilder.inTimeZone(TimeZone.getTimeZone(triggerInputDTO.getTimeZone()));
    return applyCronMisfireInstruction(scheduleBuilder, triggerInputDTO.getMisfireInstruction());
  }

  private DailyTimeIntervalScheduleBuilder buildDailyTimeIntervalSchedule(TriggerInputDTO triggerInputDTO) {
    DailyTimeIntervalScheduleBuilder scheduleBuilder = DailyTimeIntervalScheduleBuilder.dailyTimeIntervalSchedule()
      .withInterval(Math.toIntExact(triggerInputDTO.getRepeatInterval()), parseIntervalUnit(triggerInputDTO.getRepeatIntervalUnit(), DateBuilder.IntervalUnit.MINUTE));
    if (triggerInputDTO.getStartTimeOfDay() != null && !triggerInputDTO.getStartTimeOfDay().isBlank())
      scheduleBuilder.startingDailyAt(parseTimeOfDay(triggerInputDTO.getStartTimeOfDay()));
    if (triggerInputDTO.getEndTimeOfDay() != null && !triggerInputDTO.getEndTimeOfDay().isBlank())
      scheduleBuilder.endingDailyAt(parseTimeOfDay(triggerInputDTO.getEndTimeOfDay()));
    if (triggerInputDTO.getDaysOfWeek() != null && !triggerInputDTO.getDaysOfWeek().isEmpty())
      scheduleBuilder.onDaysOfTheWeek(triggerInputDTO.getDaysOfWeek());
    return applyDailyMisfireInstruction(scheduleBuilder, triggerInputDTO.getMisfireInstruction());
  }

  private CalendarIntervalScheduleBuilder buildCalendarIntervalSchedule(TriggerInputDTO triggerInputDTO) {
    CalendarIntervalScheduleBuilder scheduleBuilder = CalendarIntervalScheduleBuilder.calendarIntervalSchedule()
      .withInterval(Math.toIntExact(triggerInputDTO.getRepeatInterval()), parseIntervalUnit(triggerInputDTO.getRepeatIntervalUnit(), DateBuilder.IntervalUnit.DAY));
    if (Boolean.TRUE.equals(triggerInputDTO.getPreserveHourOfDayAcrossDaylightSavings()))
      scheduleBuilder.preserveHourOfDayAcrossDaylightSavings(true);
    if (Boolean.TRUE.equals(triggerInputDTO.getSkipDayIfHourDoesNotExist()))
      scheduleBuilder.skipDayIfHourDoesNotExist(true);
    if (triggerInputDTO.getTimeZone() != null && !triggerInputDTO.getTimeZone().isBlank())
      scheduleBuilder.inTimeZone(TimeZone.getTimeZone(triggerInputDTO.getTimeZone()));
    return applyCalendarIntervalMisfireInstruction(scheduleBuilder, triggerInputDTO.getMisfireInstruction());
  }

  private JobKey getJobKey(TriggerInputDTO triggerInputDTO) {
    JobKeyDTO jobKeyDTO = triggerInputDTO.getJobKey();
    if (jobKeyDTO == null)
      return null;
    return JobKey.jobKey(jobKeyDTO.getName(), jobKeyDTO.getGroup());
  }

  private TriggerDTO convertTrigger(Trigger trigger) {
    TriggerDTO triggerDTO = conversionService.convert(trigger, TriggerDTO.class);
    if (triggerDTO == null)
      triggerDTO = new TriggerDTO();
    if (trigger instanceof SimpleTrigger simpleTrigger)
      enrichSimpleTrigger(triggerDTO, simpleTrigger);
    else if (trigger instanceof CronTrigger cronTrigger)
      enrichCronTrigger(triggerDTO, cronTrigger);
    else if (trigger instanceof DailyTimeIntervalTrigger dailyTimeIntervalTrigger)
      enrichDailyTimeIntervalTrigger(triggerDTO, dailyTimeIntervalTrigger);
    else if (trigger instanceof CalendarIntervalTrigger calendarIntervalTrigger)
      enrichCalendarIntervalTrigger(triggerDTO, calendarIntervalTrigger);
    return triggerDTO;
  }

  private void enrichSimpleTrigger(TriggerDTO triggerDTO, SimpleTrigger simpleTrigger) {
    triggerDTO.setRepeatCount(simpleTrigger.getRepeatCount());
    triggerDTO.setRepeatInterval(simpleTrigger.getRepeatInterval());
  }

  private void enrichCronTrigger(TriggerDTO triggerDTO, CronTrigger cronTrigger) {
    triggerDTO.setCronExpression(cronTrigger.getCronExpression());
    triggerDTO.setTimeZone(cronTrigger.getTimeZone().getID());
  }

  private void enrichDailyTimeIntervalTrigger(TriggerDTO triggerDTO, DailyTimeIntervalTrigger dailyTrigger) {
    triggerDTO.setRepeatCount(dailyTrigger.getRepeatCount());
    triggerDTO.setRepeatInterval((long) dailyTrigger.getRepeatInterval());
    triggerDTO.setRepeatIntervalUnit(dailyTrigger.getRepeatIntervalUnit().name());
    triggerDTO.setStartTimeOfDay(formatTimeOfDay(dailyTrigger.getStartTimeOfDay()));
    triggerDTO.setEndTimeOfDay(formatTimeOfDay(dailyTrigger.getEndTimeOfDay()));
    triggerDTO.setDaysOfWeek(dailyTrigger.getDaysOfWeek());
  }

  private void enrichCalendarIntervalTrigger(TriggerDTO triggerDTO, CalendarIntervalTrigger calendarTrigger) {
    triggerDTO.setRepeatInterval((long) calendarTrigger.getRepeatInterval());
    triggerDTO.setRepeatIntervalUnit(calendarTrigger.getRepeatIntervalUnit().name());
    triggerDTO.setPreserveHourOfDayAcrossDaylightSavings(calendarTrigger.isPreserveHourOfDayAcrossDaylightSavings());
    triggerDTO.setSkipDayIfHourDoesNotExist(calendarTrigger.isSkipDayIfHourDoesNotExist());
    triggerDTO.setTimeZone(calendarTrigger.getTimeZone().getID());
  }

  private MisfireInstruction parseSimpleMisfireInstruction(String misfireInstruction) {
    if (misfireInstruction == null || misfireInstruction.isBlank())
      return MisfireInstruction.MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT;
    return MisfireInstruction.valueOf(misfireInstruction);
  }

  private CronScheduleBuilder applyCronMisfireInstruction(CronScheduleBuilder scheduleBuilder, String misfireInstruction) {
    return switch (normalizeMisfireInstruction(misfireInstruction)) {
      case MISFIRE_DO_NOTHING -> scheduleBuilder.withMisfireHandlingInstructionDoNothing();
      case MISFIRE_IGNORE_MISFIRES -> scheduleBuilder.withMisfireHandlingInstructionIgnoreMisfires();
      default -> scheduleBuilder.withMisfireHandlingInstructionFireAndProceed();
    };
  }

  private DailyTimeIntervalScheduleBuilder applyDailyMisfireInstruction(DailyTimeIntervalScheduleBuilder scheduleBuilder, String misfireInstruction) {
    return switch (normalizeMisfireInstruction(misfireInstruction)) {
      case MISFIRE_DO_NOTHING -> scheduleBuilder.withMisfireHandlingInstructionDoNothing();
      case MISFIRE_IGNORE_MISFIRES -> scheduleBuilder.withMisfireHandlingInstructionIgnoreMisfires();
      default -> scheduleBuilder.withMisfireHandlingInstructionFireAndProceed();
    };
  }

  private CalendarIntervalScheduleBuilder applyCalendarIntervalMisfireInstruction(CalendarIntervalScheduleBuilder scheduleBuilder, String misfireInstruction) {
    return switch (normalizeMisfireInstruction(misfireInstruction)) {
      case MISFIRE_DO_NOTHING -> scheduleBuilder.withMisfireHandlingInstructionDoNothing();
      case MISFIRE_IGNORE_MISFIRES -> scheduleBuilder.withMisfireHandlingInstructionIgnoreMisfires();
      default -> scheduleBuilder.withMisfireHandlingInstructionFireAndProceed();
    };
  }

  private String normalizeMisfireInstruction(String misfireInstruction) {
    return misfireInstruction == null || misfireInstruction.isBlank() ? "FIRE_AND_PROCEED" : misfireInstruction;
  }

  private DateBuilder.IntervalUnit parseIntervalUnit(String intervalUnit, DateBuilder.IntervalUnit defaultUnit) {
    return intervalUnit == null || intervalUnit.isBlank() ? defaultUnit : DateBuilder.IntervalUnit.valueOf(intervalUnit);
  }

  private TimeOfDay parseTimeOfDay(String timeOfDay) {
    String[] parts = timeOfDay.split(":");
    int hour = Integer.parseInt(parts[0]);
    int minute = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
    int second = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;
    return TimeOfDay.hourMinuteAndSecondOfDay(hour, minute, second);
  }

  private String formatTimeOfDay(TimeOfDay timeOfDay) {
    if (timeOfDay == null)
      return null;
    return String.format("%02d:%02d:%02d", timeOfDay.getHour(), timeOfDay.getMinute(), timeOfDay.getSecond());
  }

}
