package it.fabioformosa.quartzmanager.api.services;

import it.fabioformosa.quartzmanager.api.common.utils.DateUtils;
import it.fabioformosa.quartzmanager.api.dto.*;
import it.fabioformosa.quartzmanager.api.exceptions.TriggerNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.quartz.*;
import org.springframework.core.convert.ConversionService;

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.MockitoAnnotations.openMocks;

class SimpleTriggerServiceTest {

  @InjectMocks
  private SimpleTriggerService simpleSchedulerService;

  @Mock
  private Scheduler scheduler;

  @Mock
  private ConversionService conversionService;

  @BeforeEach
  void setUp() {
    openMocks(this);
  }

  @Test
  void givenANotExistingTrigger_whenGetSimplerTriggerByNameIsCalled_thenThrowException() throws SchedulerException {
    String not_existing_trigger = "not_existing_trigger";
    Mockito.when(scheduler.getTrigger(any())).thenReturn(null);

    Throwable throwable = Assertions.catchThrowable(() -> simpleSchedulerService.getSimpleTriggerByName(not_existing_trigger));
    Assertions.assertThat(throwable).isInstanceOf(TriggerNotFoundException.class);
  }

  @Test
  void givenAnExistingTrigger_whenGetSimplerTriggerByNameIsCalled_thenTheDtoIsReturned() throws SchedulerException, TriggerNotFoundException {
    String existing_trigger = "existing_trigger";
    Mockito.when(scheduler.getTrigger(any(TriggerKey.class)))
      .thenReturn(TriggerBuilder.newTrigger().withIdentity(existing_trigger).build());
    Mockito.when(conversionService.convert(any(SimpleTrigger.class), eq(SimpleTriggerDTO.class)))
      .thenReturn(SimpleTriggerDTO.builder()
        .triggerKeyDTO(TriggerKeyDTO.builder().name(existing_trigger).build())
        .build());


    SimpleTriggerDTO simpleTriggerByName = simpleSchedulerService.getSimpleTriggerByName(existing_trigger);
    Assertions.assertThat(simpleTriggerByName.getTriggerKeyDTO().getName()).isEqualTo(existing_trigger);
  }

  @Test
  void givenASimpleTriggerCommandDTO_whenASimpleTriggerIsScheduled_thenATriggerDTOIsReturned() throws SchedulerException, ClassNotFoundException {
    SimpleTriggerInputDTO triggerInputDTO = SimpleTriggerInputDTO.builder()
      .jobClass("it.fabioformosa.quartzmanager.api.jobs.SampleJob")
      .startDate(new Date())
      .repeatInterval(5000L).repeatCount(5)
      .endDate(DateUtils.addHoursToNow(1))
      .build();

    String simpleTriggerName = "simpleTrigger";

    SimpleTriggerDTO expectedTriggerDTO = SimpleTriggerDTO.builder()
      .startTime(triggerInputDTO.getStartDate())
      .repeatInterval(1000)
      .repeatCount(10)
      .mayFireAgain(true)
      .finalFireTime(triggerInputDTO.getEndDate())
      .jobKeyDTO(JobKeyDTO.builder().name("MyJob").build())
      .misfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW)
      .triggerKeyDTO(TriggerKeyDTO.builder().name(simpleTriggerName).build())
      .build();

    Mockito.when(scheduler.scheduleJob(any(), any())).thenReturn(new Date());
    Mockito.when(conversionService.convert(any(), eq(SimpleTriggerDTO.class))).thenReturn(expectedTriggerDTO);

    SimpleTriggerCommandDTO simpleTriggerCommandDTO = SimpleTriggerCommandDTO.builder()
      .triggerName(simpleTriggerName)
      .simpleTriggerInputDTO(triggerInputDTO)
      .build();
    SimpleTriggerDTO simpleTrigger = simpleSchedulerService.scheduleSimpleTrigger(simpleTriggerCommandDTO);

    Assertions.assertThat(simpleTrigger).isEqualTo(expectedTriggerDTO);
  }

  @Test
  void givenASimpleTriggerCommandDTO_whenASimpleTriggerIsRecheduled_thenATriggerDTOIsReturned() throws SchedulerException, ClassNotFoundException {
    SimpleTriggerInputDTO triggerInputDTO = SimpleTriggerInputDTO.builder()
      .jobClass("it.fabioformosa.quartzmanager.api.jobs.SampleJob")
      .startDate(new Date())
      .repeatInterval(5000L).repeatCount(5)
      .endDate(DateUtils.addHoursToNow(1))
      .build();

    String simpleTriggerName = "simpleTrigger";

    SimpleTriggerDTO expectedTriggerDTO = SimpleTriggerDTO.builder()
      .startTime(triggerInputDTO.getStartDate())
      .repeatInterval(1000)
      .repeatCount(10)
      .mayFireAgain(true)
      .finalFireTime(triggerInputDTO.getEndDate())
      .jobKeyDTO(JobKeyDTO.builder().name("MyJob").build())
      .misfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW)
      .triggerKeyDTO(TriggerKeyDTO.builder().name(simpleTriggerName).build())
      .build();

    Mockito.when(scheduler.rescheduleJob(any(), any())).thenReturn(new Date());
    Mockito.when(conversionService.convert(any(), eq(SimpleTriggerDTO.class))).thenReturn(expectedTriggerDTO);

    SimpleTriggerCommandDTO simpleTriggerCommandDTO = SimpleTriggerCommandDTO.builder()
      .triggerName(simpleTriggerName)
      .simpleTriggerInputDTO(triggerInputDTO)
      .build();
    SimpleTriggerDTO simpleTrigger = simpleSchedulerService.rescheduleSimpleTrigger(simpleTriggerCommandDTO);

    Assertions.assertThat(simpleTrigger).isEqualTo(expectedTriggerDTO);

    Mockito.verify(scheduler).rescheduleJob(any(), any());
    Mockito.verify(conversionService).convert(any(), eq(SimpleTriggerDTO.class));
  }

}
