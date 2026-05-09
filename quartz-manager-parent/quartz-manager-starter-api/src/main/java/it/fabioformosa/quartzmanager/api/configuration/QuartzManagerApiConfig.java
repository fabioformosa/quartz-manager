package it.fabioformosa.quartzmanager.api.configuration;

import it.fabioformosa.metamorphosis.core.converters.AbstractBaseConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;

import java.lang.reflect.Field;
import java.util.List;

@ComponentScan(basePackages = {"it.fabioformosa.quartzmanager.api"})
@Configuration
public class QuartzManagerApiConfig {

  @Bean
  public ConversionService conversionService(List<Converter<?, ?>> converters) {
    DefaultConversionService conversionService = new DefaultConversionService();
    converters.forEach(conversionService::addConverter);
    converters.stream()
      .filter(AbstractBaseConverter.class::isInstance)
      .map(AbstractBaseConverter.class::cast)
      .forEach(converter -> setConversionService(converter, conversionService));
    return conversionService;
  }

  private void setConversionService(AbstractBaseConverter<?, ?> converter, ConversionService conversionService) {
    try {
      Field conversionServiceField = AbstractBaseConverter.class.getDeclaredField("conversionService");
      conversionServiceField.setAccessible(true);
      conversionServiceField.set(converter, conversionService);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new IllegalStateException("Unable to initialize Quartz Manager converters", e);
    }
  }
}
