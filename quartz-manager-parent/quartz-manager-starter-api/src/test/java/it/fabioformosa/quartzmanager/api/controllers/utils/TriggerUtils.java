package it.fabioformosa.quartzmanager.api.controllers.utils;

import it.fabioformosa.quartzmanager.api.common.utils.DateUtils;
import it.fabioformosa.quartzmanager.api.dto.*;

import java.time.LocalDateTime;

public class TriggerUtils {

  static public TriggerDTO getTriggerInstance(String triggerName){
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

  static public SimpleTriggerDTO getSimpleTriggerInstance(String triggerName, SimpleTriggerInputDTO simpleTriggerInputDTO){
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
      .build();
  }

  static public SimpleTriggerDTO getSimpleTriggerInstance(String triggerName){
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
      .build();
  }

}
