package it.fabioformosa.quartzmanager.api.security.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.security.SecureRandom;
import java.util.Base64;


@Configuration
@ConfigurationProperties(prefix = "quartz-manager.security.jwt")
@Data
@AllArgsConstructor
@Getter
@Setter
public class JwtSecurityProperties {
  private String secret;

  private long expirationInSec = 28800;

  private CookieStrategy cookieStrategy = new CookieStrategy();
  private HeaderStrategy headerStrategy = new HeaderStrategy();

  public JwtSecurityProperties() {
    SecureRandom random = new SecureRandom();
    byte[] bytes = new byte[20];
    random.nextBytes(bytes);
    Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
    secret = encoder.encodeToString(bytes);
  }

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
