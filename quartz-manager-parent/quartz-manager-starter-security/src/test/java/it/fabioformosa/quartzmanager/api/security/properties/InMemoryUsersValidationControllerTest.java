package it.fabioformosa.quartzmanager.api.security.properties;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;

import javax.validation.Validation;
import javax.validation.Validator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class InMemoryUsersValidationControllerTest {

  private static Validator propertyValidator;

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


  @BeforeAll
  public static void setup() {
    propertyValidator = Validation.buildDefaultValidatorFactory().getValidator();
  }

  static Stream<Arguments> getNotValidInMemoryProps(){
    return notValidInMemoryProps;
  }

  @ParameterizedTest
  @MethodSource("it.fabioformosa.quartzmanager.api.security.properties.InMemoryUsersValidationControllerTest#getNotValidInMemoryProps")
  void givenAMissingUsername_whenThePropertyValidationIsApplied_thenShouldRaiseValidationError(Map<String, String> properties) throws Exception {
    ConfigurationPropertySource source = new MapConfigurationPropertySource(properties);

    Binder binder = new Binder(source);
    BindResult<InMemoryAccountProperties> result = binder.bind("quartz-manager.security.accounts.in-memory", InMemoryAccountProperties.class);

    Assertions.assertThat(result.isBound()).isTrue();

    InMemoryAccountProperties inMemoryAccountProperties = result.get();
    Assertions.assertThat(propertyValidator.validate(inMemoryAccountProperties).size()).isPositive();

  }

  @Test
  void givenAllInMemoryPropsAreSet_whenThePropertyValidationIsApplied_thenShouldRaiseValidationError() throws Exception {
    Map<String, String> properties = new HashMap<>();
    properties.put("quartz-manager.security.accounts.in-memory.users[0].username", "foo");
    properties.put("quartz-manager.security.accounts.in-memory.users[0].password", "bar");
    properties.put("quartz-manager.security.accounts.in-memory.users[0].roles[0]", "admin");

    ConfigurationPropertySource source = new MapConfigurationPropertySource(properties);

    Binder binder = new Binder(source);
    BindResult<InMemoryAccountProperties> result = binder.bind("quartz-manager.security.accounts.in-memory", InMemoryAccountProperties.class);

    Assertions.assertThat(result.isBound()).isTrue();

    InMemoryAccountProperties inMemoryAccountProperties = result.get();
    Assertions.assertThat(propertyValidator.validate(inMemoryAccountProperties).size()).isZero();
  }

}
