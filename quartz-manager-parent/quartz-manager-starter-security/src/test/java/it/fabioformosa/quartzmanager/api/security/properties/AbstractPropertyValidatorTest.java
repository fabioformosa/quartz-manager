package it.fabioformosa.quartzmanager.api.security.properties;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;

import javax.validation.Validation;
import javax.validation.Validator;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public abstract class AbstractPropertyValidatorTest {
  protected static Validator propertyValidator;

  @BeforeAll
  public static void setup() {
    propertyValidator = Validation.buildDefaultValidatorFactory().getValidator();
  }

  protected static <T> T inflateConfigurationPropertyFromAMap(Map<String, String> properties, String configurationPropName, Class<T> propClass) {
    ConfigurationPropertySource source = new MapConfigurationPropertySource(properties);
    Binder binder = new Binder(source);
    BindResult<T> result = binder.bind(configurationPropName, propClass);
    if (properties != null && !properties.isEmpty()) {
      Assertions.assertThat(result.isBound()).isTrue();
      T configPropObject = result.get();
      return configPropObject;
    } else {
      try {
        return propClass.getConstructor().newInstance();
      } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
