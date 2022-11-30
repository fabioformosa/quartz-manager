package it.fabioformosa.quartzmanager.api.services;

import it.fabioformosa.quartzmanager.api.dto.SchedulerDTO;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SchedulerServiceIntegrationTest {

  @Autowired
  private SchedulerService schedulerService;

  @Autowired
  private Scheduler scheduler;

  @Test
  void givenASchedulerService_whenGetSchedulerIsCalled_thenReturnIt() throws SchedulerException {
    SchedulerDTO schedulerDTO = schedulerService.getScheduler();
    Assertions.assertThat(schedulerDTO).isNotNull();
    Assertions.assertThat(schedulerDTO.getName()).isEqualTo(scheduler.getSchedulerName());
  }

  @Test
  void givenASchedulerService_whenTheStatusIsChange_thenTheSchedulerReflectsTheSame() throws SchedulerException {
    Assertions.assertThat(scheduler.isStarted()).isFalse();
    schedulerService.start();
    Assertions.assertThat(scheduler.isStarted()).isTrue();

    Assertions.assertThat(scheduler.isInStandbyMode()).isFalse();
    schedulerService.standby();
    Assertions.assertThat(scheduler.isInStandbyMode()).isTrue();

    Assertions.assertThat(scheduler.isShutdown()).isFalse();
    schedulerService.shutdown();
    Assertions.assertThat(scheduler.isShutdown()).isTrue();

  }

//  @Test
//  void givenASchedulerService_whenStandByIsCalled_thenTheStandByIsPropagated() throws SchedulerException {
//    Assertions.assertThat(scheduler.isInStandbyMode()).isTrue();
//    schedulerService.start();
//    Assertions.assertThat(scheduler.isInStandbyMode()).isFalse();
//    schedulerService.standby();
//    Assertions.assertThat(scheduler.isInStandbyMode()).isTrue();
//  }
//
//  @Test
//  void givenASchedulerService_whenShutdownIsCalled_thenTheShutdownIsPropagated() throws SchedulerException {
//    Assertions.assertThat(scheduler.isShutdown()).isFalse();
//    schedulerService.start();
//    schedulerService.shutdown();
//    Assertions.assertThat(scheduler.isShutdown()).isTrue();
//  }

}
