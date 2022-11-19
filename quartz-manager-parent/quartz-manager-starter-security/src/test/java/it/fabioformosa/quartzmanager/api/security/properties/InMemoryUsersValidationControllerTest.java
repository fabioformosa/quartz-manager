package it.fabioformosa.quartzmanager.api.security.properties;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

class InMemoryUsersValidationControllerTest extends AbstractPropertyValidatorTest {

  static Stream<Arguments> notValidInMemoryProps = Stream.of(
    Arguments.of(
      Map.of("quartz-manager.security.accounts.in-memory.users[0].password", "bar"),
      Map.of("quartz-manager.security.accounts.in-memory.users[0].roles[0]", "admin")),
    Arguments.of(
      Map.of("quartz-manager.security.accounts.in-memory.users[0].username", "foo"),
      Map.of("quartz-manager.security.accounts.in-memory.users[0].roles[0]", "admin")),
    Arguments.of(
      Map.of("quartz-manager.security.accounts.in-memory.users[0].username", "foo"),
      Map.of("quartz-manager.security.accounts.in-memory.users[0].password", "bar"))
  );


  static Stream<Arguments> getNotValidInMemoryProps() {
    return notValidInMemoryProps;
  }

  @ParameterizedTest
  @MethodSource("it.fabioformosa.quartzmanager.api.security.properties.InMemoryUsersValidationControllerTest#getNotValidInMemoryProps")
  void givenAMissingUsername_whenThePropertyValidationIsApplied_thenShouldRaiseValidationError(Map<String, String> properties) {
    InMemoryAccountProperties inMemoryAccountProperties = inflateConfigurationPropertyFromAMap(properties,
      "quartz-manager.security.accounts.in-memory", InMemoryAccountProperties.class);
    Assertions.assertThat(propertyValidator.validate(inMemoryAccountProperties)).isNotEmpty();

  }

  @Test
  void givenAllInMemoryPropsAreSet_whenThePropertyValidationIsApplied_thenShouldRaiseValidationError() throws Exception {
    Map<String, String> properties = new HashMap<>();
    properties.put("quartz-manager.security.accounts.in-memory.users[0].username", "foo");
    properties.put("quartz-manager.security.accounts.in-memory.users[0].password", "bar");
    properties.put("quartz-manager.security.accounts.in-memory.users[0].roles[0]", "admin");

    InMemoryAccountProperties inMemoryAccountProperties = inflateConfigurationPropertyFromAMap(properties,
      "quartz-manager.security.accounts.in-memory", InMemoryAccountProperties.class);

    Assertions.assertThat(propertyValidator.validate(inMemoryAccountProperties)).isEmpty();
  }

}
