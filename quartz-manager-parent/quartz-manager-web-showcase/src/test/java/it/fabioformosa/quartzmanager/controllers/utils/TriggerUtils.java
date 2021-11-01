package it.fabioformosa.quartzmanager.controllers.utils;

import it.fabioformosa.quartzmanager.common.utils.DateUtils;
import it.fabioformosa.quartzmanager.dto.JobKeyDTO;
import it.fabioformosa.quartzmanager.dto.TriggerDTO;
import it.fabioformosa.quartzmanager.dto.TriggerKeyDTO;

import java.time.LocalDateTime;

public class TriggerUtils {

  static public TriggerDTO getTriggerInstance(){
    return TriggerDTO.builder()
      .description("sample trigger")
      .endTime(DateUtils.getHoursFromNow(2L))
      .finalFireTime(DateUtils.getHoursFromNow(2L))
      .jobKeyDTO(JobKeyDTO.builder()
        .group("defaultJobGroup")
        .name("sampleJob")
        .build())
      .mayFireAgain(true)
      .triggerKeyDTO(TriggerKeyDTO.builder()
        .group("defaultTriggerGroup")
        .name("sampleTrigger")
        .build())
      .misfireInstruction(1)
      .nextFireTime(DateUtils.getHoursFromNow(1L))
      .priority(1)
      .startTime(DateUtils.fromLocaleDateTimeToDate(LocalDateTime.now()))
      .build();
  }

}
