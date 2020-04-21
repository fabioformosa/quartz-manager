package it.fabioformosa.quartzmanager.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "quartz-manager.security.jwt")
@Getter @Setter
public class JwtSecurityProperties {
  private boolean enabled;
  private String secret;
  private long expirationInSec;
  private String header;
  private String cookie;
}