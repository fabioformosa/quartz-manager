package it.fabioformosa.quartzmanager.api.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@ConditionalOnClass(name = {"it.fabioformosa.quartzmanager.api.security.QuartzManagerSecurityConfig"})
@Configuration
public class SecurityDiscoverConfig {

  @Bean
  public SecurityDiscover securityIsEnabled(){
    log.info("Quartz Manager Security Layer is enabled!");
    return new SecurityDiscover();
  }

}
