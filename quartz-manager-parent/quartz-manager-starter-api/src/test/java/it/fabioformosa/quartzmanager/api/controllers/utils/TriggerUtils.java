package it.fabioformosa.quartzmanager.api.controllers.utils;

import it.fabioformosa.quartzmanager.api.common.utils.DateUtils;
import it.fabioformosa.quartzmanager.api.dto.*;
import org.quartz.JobDataMap;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

import static java.util.Map.entry;

public class TriggerUtils {

  static public TriggerDTO getTriggerInstance(String triggerName) {
    return TriggerDTO.builder()
      .description("sample trigger")
      .endTime(DateUtils.addHoursToNow(2L))
      .finalFireTime(DateUtils.addHoursToNow(2L))
      .jobKeyDTO(JobKeyDTO.builder()
        .group("defaultJobGroup")
        .name("sampleJob")
        .build())
      .mayFireAgain(true)
      .triggerKeyDTO(TriggerKeyDTO.builder()
        .group("defaultTriggerGroup")
        .name(triggerName)
        .build())
      .misfireInstruction(1)
      .nextFireTime(DateUtils.addHoursToNow(1L))
      .priority(1)
      .startTime(DateUtils.fromLocalDateTimeToDate(LocalDateTime.now()))
      .build();
  }

  static public SimpleTriggerDTO getSimpleTriggerInstance(String triggerName, SimpleTriggerInputDTO simpleTriggerInputDTO) {
    return SimpleTriggerDTO.builder()
      .description("simple trigger")
      .repeatCount(simpleTriggerInputDTO.getRepeatCount())
      .repeatInterval(simpleTriggerInputDTO.getRepeatInterval())
      .endTime(DateUtils.addHoursToNow(2L))
      .finalFireTime(DateUtils.addHoursToNow(2L))
      .jobKeyDTO(JobKeyDTO.builder()
        .group("defaultJobGroup")
        .name("sampleJob")
        .build())
      .mayFireAgain(true)
      .triggerKeyDTO(TriggerKeyDTO.builder()
        .group("defaultTriggerGroup")
        .name(triggerName)
        .build())
      .misfireInstruction(1)
      .nextFireTime(DateUtils.addHoursToNow(1L))
      .priority(1)
      .startTime(DateUtils.fromLocalDateTimeToDate(LocalDateTime.now()))
      .jobDataMap(new JobDataMap(simpleTriggerInputDTO.getJobDataMap()))
      .build();
  }

  static public SimpleTriggerDTO getSimpleTriggerInstance(String triggerName) {
    return SimpleTriggerDTO.builder()
      .description("simple trigger")
      .repeatCount(2)
      .repeatInterval(1000L)
      .endTime(DateUtils.addHoursToNow(2L))
      .finalFireTime(DateUtils.addHoursToNow(2L))
      .jobKeyDTO(JobKeyDTO.builder()
        .group("defaultJobGroup")
        .name("sampleJob")
        .build())
      .mayFireAgain(true)
      .triggerKeyDTO(TriggerKeyDTO.builder()
        .group("defaultTriggerGroup")
        .name(triggerName)
        .build())
      .misfireInstruction(1)
      .nextFireTime(DateUtils.addHoursToNow(1L))
      .priority(1)
      .startTime(DateUtils.fromLocalDateTimeToDate(LocalDateTime.now()))
      .jobDataMap(new JobDataMap(Map.ofEntries(entry("customTriggerData1", "value1"))))
      .build();
  }

  static public SimpleTrigger buildSimpleTrigger() {
    TriggerBuilder<Trigger> triggerTriggerBuilder = TriggerBuilder.newTrigger();
    triggerTriggerBuilder.startAt(new Date());
    triggerTriggerBuilder.endAt(DateUtils.addHoursToNow(1));
    triggerTriggerBuilder.usingJobData(new JobDataMap(Map.ofEntries(entry("data", "value"))));

    SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule();
    scheduleBuilder.withIntervalInMilliseconds(1000);
    scheduleBuilder.withRepeatCount(1);
    scheduleBuilder.withMisfireHandlingInstructionFireNow();

    return triggerTriggerBuilder.withSchedule(
        scheduleBuilder
      )
      .withIdentity("simpleTrigger").build();
  }

  static public SimpleTriggerCommandDTO buildSimpleTriggerCommandDTO(String triggerName) throws ParseException {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Date startDate = dateFormat.parse("2024-02-02");
    Date endDate = dateFormat.parse("2024-03-02");

    SimpleTriggerInputDTO triggerInputDTO = SimpleTriggerInputDTO.builder()
      .misfireInstruction(MisfireInstruction.MISFIRE_INSTRUCTION_FIRE_NOW)
      .jobClass("sample.jobClass")
      .repeatCount(1)
      .repeatInterval(1000L)
      .startDate(startDate)
      .endDate(endDate)
      .jobDataMap(Map.ofEntries(entry("data", "value")))
      .build();
    return SimpleTriggerCommandDTO.builder()
      .triggerName(triggerName)
      .simpleTriggerInputDTO(triggerInputDTO)
      .build();
  }

}
