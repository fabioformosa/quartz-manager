package it.fabioformosa.quartzmanager.api.controllers.utils;

import it.fabioformosa.quartzmanager.api.common.utils.DateUtils;
import it.fabioformosa.quartzmanager.api.dto.SimpleTriggerInputDTO;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.Date;
import java.util.stream.Stream;

public class InvalidSimpleTriggerCommandDTOProvider implements ArgumentsProvider {
  @Override
  public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
    return Stream.of(
      Arguments.of(buildSimpleTriggerWithBlankMandatoryFields()),
      Arguments.of(buildSimpleTriggerWithRepeatCountAndWithoutRepeatInterval()),
      Arguments.of(buildSimpleTriggerWithRepeatIntervalAndWithoutRepeatCount()),
      Arguments.of(buildSimpleTriggerWithNegativeRepeatInterval()),
      Arguments.of(buildSimpleTriggerWithInvalidTriggerPeriod())
    );
  }

  private SimpleTriggerInputDTO buildSimpleTriggerWithNegativeRepeatInterval() {
    return minimalSimpleTriggerBuilder().repeatInterval(-2000L).repeatCount(10).build();
  }

  private static SimpleTriggerInputDTO buildSimpleTriggerWithRepeatIntervalAndWithoutRepeatCount() {
    return minimalSimpleTriggerBuilder().repeatInterval(1L).build();
  }

  private static SimpleTriggerInputDTO.SimpleTriggerInputDTOBuilder<?, ?> minimalSimpleTriggerBuilder() {
    return SimpleTriggerInputDTO.builder()
      .jobClass("it.fabioformosa.quartzmanager.api.jobs.SampleJob");
  }

  private static SimpleTriggerInputDTO buildSimpleTriggerWithRepeatCountAndWithoutRepeatInterval() {
    return minimalSimpleTriggerBuilder().repeatCount(1).build();
  }

  private static SimpleTriggerInputDTO buildSimpleTriggerWithBlankMandatoryFields() {
    return SimpleTriggerInputDTO.builder().build();
  }

  private static SimpleTriggerInputDTO buildSimpleTriggerWithInvalidTriggerPeriod() {
    return minimalSimpleTriggerBuilder().endDate(new Date()).startDate(DateUtils.addHoursToNow(1)).build();
  }

}
