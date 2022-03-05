package it.fabioformosa.quartzmanager.services;

import it.fabioformosa.quartzmanager.common.utils.DateUtils;
import it.fabioformosa.quartzmanager.dto.*;
import it.fabioformosa.quartzmanager.exceptions.TriggerNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.springframework.core.convert.ConversionService;

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.MockitoAnnotations.openMocks;

class SimpleTriggerSchedulerServiceTest {

  @InjectMocks
  private SimpleTriggerSchedulerService simpleSchedulerService;

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
  void givenASimpleTriggerCommandDTO_whenASimpleTriggerIsScheduled_thenATriggerDTOIsReturned() throws SchedulerException, ClassNotFoundException {
    SimpleTriggerInputDTO triggerInputDTO = SimpleTriggerInputDTO.builder()
      .startDate(new Date())
      .repeatInterval(5000L).repeatCount(5)
      .endDate(DateUtils.getHoursFromNow(1))
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
    SimpleTriggerDTO simpleTrigger = simpleSchedulerService.scheduleSimpleTrigger("it.fabioformosa.quartzmanager.jobs.SampleJob", simpleTriggerCommandDTO);

    Assertions.assertThat(simpleTrigger).isEqualTo(expectedTriggerDTO);
  }

}
