package it.fabioformosa.quartzmanager.api.services;

import it.fabioformosa.quartzmanager.api.dto.SchedulerDTO;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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

  @Order(1)
  @Test
  void givenASchedulerService_whenTheStatusIsChange_thenTheSchedulerReflectsTheSame() throws SchedulerException {
    Assertions.assertThat(scheduler.isStarted()).isFalse();
    schedulerService.start();
    Assertions.assertThat(scheduler.isStarted()).isTrue();
  }
  @Order(2)
  @Test
  void givenASchedulerService_whenStandByIsCalled_thenTheStandByIsPropagated() throws SchedulerException {
    Assertions.assertThat(scheduler.isInStandbyMode()).isFalse();
    schedulerService.standby();
    Assertions.assertThat(scheduler.isInStandbyMode()).isTrue();
  }

  @Order(3)
  @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
  @Test
  void givenASchedulerService_whenShutdownIsCalled_thenTheShutdownIsPropagated() throws SchedulerException {
    Assertions.assertThat(scheduler.isShutdown()).isFalse();
    schedulerService.start();
    schedulerService.shutdown();
    Assertions.assertThat(scheduler.isShutdown()).isTrue();
  }

}
