package it.fabioformosa.quartzmanager.api.persistence;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Properties;

@Configuration
@PropertySource("classpath:quartz-persistence.properties")
@ConfigurationProperties(prefix = "spring.quartz")
@Getter @Setter
public class QuartzPersistencePropConfig {
  private Properties properties;
}
