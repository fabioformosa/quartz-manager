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

  private static final String SAMPLE_JOB_CLASS = "it.fabioformosa.quartzmanager.api.jobs.SampleJob";
  private static final String SAMPLE_EXTRA_JOB_CLASS = "it.fabioformosa.samplepackage.SampleExtraJob";
  private static final String FIRST_TRIGGER_SUFFIX = "A";
  private static final String SECOND_TRIGGER_SUFFIX = "B";

  @Autowired
  private SimpleTriggerService simpleTriggerService;

  @Test
  void givenASimpleTriggerCommandDTOWithAllData_whenANewSimpleTriggerIsScheduled_thenShouldGetATriggertDTO() throws SchedulerException, ClassNotFoundException {
    String simpleTriggerTestName = "simpleTriggerWithAllData";
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
        .jobClass(SAMPLE_JOB_CLASS)
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

    SimpleTriggerCommandDTO simpleTriggerCommand = SimpleTriggerCommandDTO.builder()
      .triggerName(simpleTriggerTestName)
      .simpleTriggerInputDTO(SimpleTriggerInputDTO.builder()
        .jobClass(SAMPLE_JOB_CLASS)
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

  @Test
  void givenTwoSimpleTriggerCommandDTOsForTheSameJob_whenScheduled_thenShouldCreateTwoTriggers() throws SchedulerException, ClassNotFoundException {
    String triggerNamePrefix = "sameJobTrigger" + System.nanoTime();

    SimpleTriggerDTO firstTrigger = simpleTriggerService.scheduleSimpleTrigger(
      buildSimpleTriggerCommand(triggerNamePrefix + FIRST_TRIGGER_SUFFIX, SAMPLE_JOB_CLASS)
    );
    SimpleTriggerDTO secondTrigger = simpleTriggerService.scheduleSimpleTrigger(
      buildSimpleTriggerCommand(triggerNamePrefix + SECOND_TRIGGER_SUFFIX, SAMPLE_JOB_CLASS)
    );

    Assertions.assertThat(firstTrigger.getTriggerKeyDTO().getName()).isEqualTo(triggerNamePrefix + FIRST_TRIGGER_SUFFIX);
    Assertions.assertThat(secondTrigger.getTriggerKeyDTO().getName()).isEqualTo(triggerNamePrefix + SECOND_TRIGGER_SUFFIX);
    Assertions.assertThat(firstTrigger.getJobDetailDTO().getJobClassName()).isEqualTo(SAMPLE_JOB_CLASS);
    Assertions.assertThat(secondTrigger.getJobDetailDTO().getJobClassName()).isEqualTo(SAMPLE_JOB_CLASS);
    Assertions.assertThat(firstTrigger.getJobKeyDTO().getName()).isNotEqualTo(secondTrigger.getJobKeyDTO().getName());
  }

  @Test
  void givenTwoSimpleTriggerCommandDTOsForDifferentJobs_whenScheduled_thenShouldCreateTwoTriggers() throws SchedulerException, ClassNotFoundException {
    String triggerNamePrefix = "differentJobTrigger" + System.nanoTime();

    SimpleTriggerDTO firstTrigger = simpleTriggerService.scheduleSimpleTrigger(
      buildSimpleTriggerCommand(triggerNamePrefix + FIRST_TRIGGER_SUFFIX, SAMPLE_JOB_CLASS)
    );
    SimpleTriggerDTO secondTrigger = simpleTriggerService.scheduleSimpleTrigger(
      buildSimpleTriggerCommand(triggerNamePrefix + SECOND_TRIGGER_SUFFIX, SAMPLE_EXTRA_JOB_CLASS)
    );

    Assertions.assertThat(firstTrigger.getTriggerKeyDTO().getName()).isEqualTo(triggerNamePrefix + FIRST_TRIGGER_SUFFIX);
    Assertions.assertThat(secondTrigger.getTriggerKeyDTO().getName()).isEqualTo(triggerNamePrefix + SECOND_TRIGGER_SUFFIX);
    Assertions.assertThat(firstTrigger.getJobDetailDTO().getJobClassName()).isEqualTo(SAMPLE_JOB_CLASS);
    Assertions.assertThat(secondTrigger.getJobDetailDTO().getJobClassName()).isEqualTo(SAMPLE_EXTRA_JOB_CLASS);
  }

  private static SimpleTriggerCommandDTO buildSimpleTriggerCommand(String triggerName, String jobClass) {
    return SimpleTriggerCommandDTO.builder()
      .triggerName(triggerName)
      .simpleTriggerInputDTO(SimpleTriggerInputDTO.builder()
        .jobClass(jobClass)
        .startDate(DateUtils.addHoursToNow(1))
        .build())
      .build();
  }

}
