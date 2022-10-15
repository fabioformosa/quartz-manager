package it.fabioformosa.quartzmanager.api.security.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Validated
@Configuration
@ConfigurationProperties(prefix = "quartz-manager.security.accounts.in-memory")
@Getter @Setter
public class InMemoryAccountProperties {
  private boolean enabled = true;

  @Valid
  @NotNull
  @NotEmpty
  private List<User> users;

  @Getter @Setter
  public static class User {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotEmpty
    private List<String> roles = new ArrayList<>();
  }
}
