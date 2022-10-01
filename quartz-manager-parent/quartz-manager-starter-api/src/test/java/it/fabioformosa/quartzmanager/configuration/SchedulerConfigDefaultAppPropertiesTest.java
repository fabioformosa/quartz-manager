package it.fabioformosa.quartzmanager.configuration;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static it.fabioformosa.quartzmanager.configuration.QuartzDefaultPropertiesConfig.QUARTZ_MANAGER_SCHEDULER_DEFAULT_NAME;

@SpringBootTest
class SchedulerConfigDefaultAppPropertiesTest {

  @Autowired
  private Scheduler scheduler;

  @Test
  void givenTheQuartzPropMissing_whenTheBootstrapOccurs_thenAQuartzInstanceShouldBeInstantiated(){
    Assertions.assertThat(scheduler).isNotNull();
  }

  @Test
  void givenTheQuartzNameMissing_whenTheBootstrapOccurs_thenAQuartzInstanceShouldBeTheDefaultName() throws SchedulerException {
    Assertions.assertThat(scheduler.getSchedulerName()).isEqualTo(QUARTZ_MANAGER_SCHEDULER_DEFAULT_NAME);
  }


}
