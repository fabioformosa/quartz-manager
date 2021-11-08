package it.fabioformosa.quartzmanager.controllers.utils;

import it.fabioformosa.quartzmanager.dto.SchedulerConfigParam;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class InvalidSchedulerConfigParamProvider implements ArgumentsProvider {
  @Override
  public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
    return Stream.of(
      Arguments.of(SchedulerConfigParam.builder().build()),
      Arguments.of(SchedulerConfigParam.builder().maxCount(1).build()),
      Arguments.of(SchedulerConfigParam.builder().triggerPerDay(1L).build())
    );
  }
}
