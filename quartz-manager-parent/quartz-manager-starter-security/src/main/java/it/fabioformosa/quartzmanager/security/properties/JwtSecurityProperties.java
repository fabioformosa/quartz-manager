package it.fabioformosa.quartzmanager.security.properties;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "quartz-manager.security.jwt")
@Getter
@Setter
public class JwtSecurityProperties {
  private String secret = RandomStringUtils.randomAlphabetic(10);
  private long expirationInSec = 28800;

  private CookieStrategy cookieStrategy = new CookieStrategy();
  private HeaderStrategy headerStrategy = new HeaderStrategy();

  @Data
  public static class CookieStrategy {
    private boolean enabled = false;
    private String cookie = "AUTH-TOKEN";
  }

  @Data
  public static class HeaderStrategy {
    private boolean enabled = true;
    private String header = "Authorization";
  }

}
