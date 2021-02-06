package it.fabioformosa.quartzmanager.security.configuration.properties;

import lombok.Data;
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
  
  private CookieStrategy cookieStrategy;
  private HeaderStrategy headerStrategy;

  @Data
  public static class CookieStrategy {
    private boolean enabled;
    private String cookie;
  }
  
  @Data
  public static class HeaderStrategy {
    private boolean enabled;
    private String header;
  }
  
}