package it.fabioformosa.quartzmanager.api.security;

import it.fabioformosa.quartzmanager.api.security.controllers.TestController;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringApplicationTest {

  @Autowired
  private TestController testController;
  @Test
  public void contextLoad(){
    Assertions.assertThat(testController).isNotNull();
  }

}
