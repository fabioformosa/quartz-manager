package it.fabioformosa.quartzmanager.configuration;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SchedulerConfigDefaultAppPropertiesTest {

  @Autowired
  private Scheduler scheduler;

  @Test
  void givenTheQuartzPropMissing_whenTheBootstrapOccurs_thenAQuartzInstanceShouldBeInstanciated(){
    Assertions.assertThat(scheduler).isNotNull();
  }


}
