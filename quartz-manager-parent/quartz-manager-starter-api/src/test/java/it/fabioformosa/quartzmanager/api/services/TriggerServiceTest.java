package it.fabioformosa.quartzmanager.api.services;

import it.fabioformosa.quartzmanager.api.dto.TriggerKeyDTO;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.springframework.core.convert.ConversionService;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

class TriggerServiceTest {

  @InjectMocks
  private TriggerService triggerService;

  @Mock
  private Scheduler scheduler;

  @Mock
  private ConversionService conversionService;

  @BeforeEach
  void setUp(){
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void givenATrigger_whenTheyAreFecthed_TheServiceReturnsTheDtos() throws SchedulerException {
    String triggerTestName = "triggerTest";
    Mockito.when(scheduler.getTriggerKeys(any())).thenReturn(Set.of(TriggerKey.triggerKey(triggerTestName)));
    Mockito.when(conversionService.convert(any(TriggerKey.class), eq(TriggerKeyDTO.class)))
      .thenReturn(TriggerKeyDTO.builder().name(triggerTestName).build());

    List<TriggerKeyDTO> triggerKeyDTOs = triggerService.fetchTriggers();
    Assertions.assertThat(triggerKeyDTOs).hasSize(1);
    Assertions.assertThat(triggerKeyDTOs.get(0).getName()).isEqualTo(triggerTestName);
  }

}
