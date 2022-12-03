package it.fabioformosa.quartzmanager.api.services;

import it.fabioformosa.quartzmanager.api.dto.SchedulerDTO;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.core.convert.ConversionService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.MockitoAnnotations.openMocks;

class SchedulerServiceTest {

  @InjectMocks
  private SchedulerService schedulerService;

  @Mock
  private Scheduler scheduler;

  @Mock
  private ConversionService conversionService;

  @BeforeEach
  void setUp() {
    openMocks(this);
  }

  @Test
  void givenASchedulerService_whenGetSchedulerIsCalled_thenReturnIt(){
    Mockito.when(conversionService.convert(any(Scheduler.class), eq(SchedulerDTO.class))).thenReturn(SchedulerDTO.builder()
      .name("testScheduler")
      .build());

    SchedulerDTO schedulerDTO = schedulerService.getScheduler();
    Assertions.assertThat(schedulerDTO).isNotNull();
    Assertions.assertThat(schedulerDTO.getName()).isEqualTo("testScheduler");
  }

  @Test
  void givenASchedulerService_whenStandByIsCalled_thenTheStandByIsPropagated() throws SchedulerException {
    schedulerService.standby();
    Mockito.verify(scheduler).standby();
  }

  @Test
  void givenASchedulerService_whenShutdownIsCalled_thenTheShutdownIsPropagated() throws SchedulerException {
    schedulerService.shutdown();
    Mockito.verify(scheduler).shutdown(true);
  }

  @Test
  void givenASchedulerService_whenStarted_thenTheSchedulerIsStarted() throws SchedulerException {
    schedulerService.start();
    Mockito.verify(scheduler).start();
  }

}
