package it.fabioformosa.quartzmanager.api.services;

import it.fabioformosa.quartzmanager.api.common.utils.DateUtils;
import it.fabioformosa.quartzmanager.api.dto.MisfireInstruction;
import it.fabioformosa.quartzmanager.api.dto.SimpleTriggerCommandDTO;
import it.fabioformosa.quartzmanager.api.dto.SimpleTriggerDTO;
import it.fabioformosa.quartzmanager.api.dto.SimpleTriggerInputDTO;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;

@SpringBootTest
class SimpleTriggerServiceIntegrationTest {

  @Autowired
  private SimpleTriggerService simpleTriggerService;

  @Test
  void givenASimpleTriggerCommandDTOWithAllData_whenANewSimpleTriggerIsScheduled_thenShouldGetATriggertDTO() throws SchedulerException, ClassNotFoundException {
    String simpleTriggerTestName = "simpleTriggerWithAllData";
    String jobClass = "it.fabioformosa.quartzmanager.api.jobs.SampleJob";
    Date startDate = new Date();
    Date endDate = DateUtils.addHoursToNow(5);
    int repeatCount = 3;
    long repeatInterval = 1000L * 60 * 60;
    LocalDateTime expectedFinalDateTime = DateUtils.fromDateToLocalDateTime(startDate).plus(Duration.ofHours(3));
    LocalDateTime expectedNextDateTime = DateUtils.fromDateToLocalDateTime(startDate).plus(Duration.ofHours(1));
    MisfireInstruction misfireInstructionFireNow = MisfireInstruction.MISFIRE_INSTRUCTION_FIRE_NOW;

    SimpleTriggerCommandDTO simpleTriggerCommand = SimpleTriggerCommandDTO.builder()
      .triggerName(simpleTriggerTestName)
      .simpleTriggerInputDTO(SimpleTriggerInputDTO.builder()
        .startDate(startDate)
        .endDate(endDate)
        .repeatCount(repeatCount)
        .repeatInterval(repeatInterval)
        .misfireInstruction(misfireInstructionFireNow)
        .jobClass(jobClass)
        .build())
      .build();
    SimpleTriggerDTO simpleTriggerDTO = simpleTriggerService.scheduleSimpleTrigger(simpleTriggerCommand);

    Assertions.assertThat(simpleTriggerDTO.getTriggerKeyDTO().getName()).isEqualTo(simpleTriggerTestName);
    Assertions.assertThat(simpleTriggerDTO.getStartTime()).isEqualTo(startDate);
    Assertions.assertThat(simpleTriggerDTO.getEndTime()).isEqualTo(endDate);
    Assertions.assertThat(simpleTriggerDTO.getRepeatCount()).isEqualTo(repeatCount);
    Assertions.assertThat(simpleTriggerDTO.getRepeatInterval()).isEqualTo(repeatInterval);
    Assertions.assertThat(simpleTriggerDTO.getMisfireInstruction()).isEqualTo(misfireInstructionFireNow.getNum());
    Assertions.assertThat(simpleTriggerDTO.getTimesTriggered()).isZero();
    Assertions.assertThat(simpleTriggerDTO.getFinalFireTime()).isEqualTo(DateUtils.fromLocalDateTimeToDate(expectedFinalDateTime));
    Assertions.assertThat(simpleTriggerDTO.getNextFireTime()).isEqualTo(startDate);
    Assertions.assertThat(simpleTriggerDTO.getJobKeyDTO().getName()).isNotNull();
  }

  @Test
  void givenASimpleTriggerCommandDTOWithMissingOptionalField_whenANewSimpleTriggerIsScheduled_thenShouldGetATriggertDTO() throws SchedulerException, ClassNotFoundException {
    String simpleTriggerTestName = "simpleTriggerWithoutOptionalData";
    String jobClass = "it.fabioformosa.quartzmanager.api.jobs.SampleJob";

    SimpleTriggerCommandDTO simpleTriggerCommand = SimpleTriggerCommandDTO.builder()
      .triggerName(simpleTriggerTestName)
      .simpleTriggerInputDTO(SimpleTriggerInputDTO.builder()
        .jobClass(jobClass)
        .build())
      .build();
    SimpleTriggerDTO simpleTriggerDTO = simpleTriggerService.scheduleSimpleTrigger(simpleTriggerCommand);

    Assertions.assertThat(simpleTriggerDTO.getTriggerKeyDTO().getName()).isEqualTo(simpleTriggerTestName);
    Assertions.assertThat(simpleTriggerDTO.getTimesTriggered()).isZero();
    Assertions.assertThat(simpleTriggerDTO.getJobKeyDTO().getName()).isNotNull();
    Assertions.assertThat(simpleTriggerDTO.getStartTime()).isNotNull();
    Assertions.assertThat(simpleTriggerDTO.getEndTime()).isNull();
    Assertions.assertThat(simpleTriggerDTO.getFinalFireTime()).isNotNull();
    Assertions.assertThat(simpleTriggerDTO.getRepeatCount()).isZero();
    Assertions.assertThat(simpleTriggerDTO.getRepeatInterval()).isZero();
  }

}
