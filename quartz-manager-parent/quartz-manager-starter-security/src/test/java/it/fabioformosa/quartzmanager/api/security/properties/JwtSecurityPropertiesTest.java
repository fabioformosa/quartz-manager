package it.fabioformosa.quartzmanager.api.security.properties;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class JwtSecurityPropertiesTest extends AbstractPropertyValidatorTest {

  @Test
  void givenAllJWTSecurityPropSet_whenThePropertyValidationIsApplied_thenShouldBeValid() {
    Map<String, String> properties = new HashMap<>();
    String secret = "helloworld";
    properties.put("quartz-manager.security.jwt.secret", secret);
    String expirationInSec = "36000";
    properties.put("quartz-manager.security.jwt.expirationInSec", expirationInSec);

    JwtSecurityProperties jwtSecurityProperties = inflateConfigurationPropertyFromAMap(properties,
      "quartz-manager.security.jwt", JwtSecurityProperties.class);

    Assertions.assertThat(propertyValidator.validate(jwtSecurityProperties)).isEmpty();

    Assertions.assertThat(jwtSecurityProperties.getExpirationInSec()).isEqualTo(Long.valueOf(expirationInSec));
    Assertions.assertThat(jwtSecurityProperties.getSecret()).isEqualTo(secret);
  }

  @Test
  void givenTheMandatoryJWTSecurityPropUnset_whenThePropertyValidationIsApplied_thenShouldBeSetWithDefault() {
    Map<String, String> properties = new HashMap<>();

    JwtSecurityProperties jwtSecurityProperties = inflateConfigurationPropertyFromAMap(properties,
      "quartz-manager.security.jwt", JwtSecurityProperties.class);

    Assertions.assertThat(jwtSecurityProperties.getExpirationInSec()).isEqualTo(28800L);
    Assertions.assertThat(jwtSecurityProperties.getSecret()).isNotBlank();
  }

}
