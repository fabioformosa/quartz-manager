package it.fabioformosa.quartzmanager.api;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("it.fabioformosa.quartzmanager")
@SpringBootConfiguration
public class QuartManagerApplicationTests {

    @Autowired
    private Scheduler scheduler;

    @Test
    public void contextLoads() {
      Assertions.assertThat(scheduler).isNotNull();
    }

}
