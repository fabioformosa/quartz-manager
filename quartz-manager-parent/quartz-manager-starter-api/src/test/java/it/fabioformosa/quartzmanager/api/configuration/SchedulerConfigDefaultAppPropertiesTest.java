package it.fabioformosa.quartzmanager.api.configuration;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SchedulerConfigDefaultAppPropertiesTest {

  @Autowired
  @Qualifier("quartzManagerScheduler")
  private Scheduler scheduler;

  @Test
  void givenTheQuartzPropMissing_whenTheBootstrapOccurs_thenAQuartzInstanceShouldBeInstantiated(){
    Assertions.assertThat(scheduler).isNotNull();
  }

  @Test
  void givenTheQuartzNameMissing_whenTheBootstrapOccurs_thenAQuartzInstanceShouldBeTheDefaultName() throws SchedulerException {
    Assertions.assertThat(scheduler.getSchedulerName()).isEqualTo(QuartzDefaultPropertiesConfig.QUARTZ_MANAGER_SCHEDULER_DEFAULT_NAME);
  }


}
