package it.fabioformosa.quartzmanager.api.controllers.utils;

import it.fabioformosa.quartzmanager.api.common.utils.DateUtils;
import it.fabioformosa.quartzmanager.api.dto.SimpleTriggerInputDTO;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.support.ParameterDeclarations;

import java.util.Date;
import java.util.stream.Stream;

public class InvalidSimpleTriggerCommandDTOProvider implements ArgumentsProvider {
  @Override
  public Stream<? extends Arguments> provideArguments(ParameterDeclarations parameters, ExtensionContext extensionContext) {
    return Stream.of(
      Arguments.of(buildSimpleTriggerWithBlankMandatoryFields()),
      Arguments.of(buildSimpleTriggerWithRepeatCountAndWithoutRepeatInterval()),
      Arguments.of(buildSimpleTriggerWithRepeatIntervalAndWithoutRepeatCount()),
      Arguments.of(buildSimpleTriggerWithNegativeRepeatInterval()),
      Arguments.of(buildSimpleTriggerWithInvalidTriggerPeriod())
    );
  }

  private SimpleTriggerInputDTO buildSimpleTriggerWithNegativeRepeatInterval() {
    SimpleTriggerInputDTO simpleTriggerInputDTO = minimalSimpleTrigger();
    simpleTriggerInputDTO.setRepeatInterval(-2000L);
    simpleTriggerInputDTO.setRepeatCount(10);
    return simpleTriggerInputDTO;
  }

  private static SimpleTriggerInputDTO buildSimpleTriggerWithRepeatIntervalAndWithoutRepeatCount() {
    SimpleTriggerInputDTO simpleTriggerInputDTO = minimalSimpleTrigger();
    simpleTriggerInputDTO.setRepeatInterval(1L);
    return simpleTriggerInputDTO;
  }

  private static SimpleTriggerInputDTO minimalSimpleTrigger() {
    SimpleTriggerInputDTO simpleTriggerInputDTO = new SimpleTriggerInputDTO();
    simpleTriggerInputDTO.setJobClass("it.fabioformosa.quartzmanager.api.jobs.SampleJob");
    return simpleTriggerInputDTO;
  }

  private static SimpleTriggerInputDTO buildSimpleTriggerWithRepeatCountAndWithoutRepeatInterval() {
    SimpleTriggerInputDTO simpleTriggerInputDTO = minimalSimpleTrigger();
    simpleTriggerInputDTO.setRepeatCount(1);
    return simpleTriggerInputDTO;
  }

  private static SimpleTriggerInputDTO buildSimpleTriggerWithBlankMandatoryFields() {
    return SimpleTriggerInputDTO.builder().build();
  }

  private static SimpleTriggerInputDTO buildSimpleTriggerWithInvalidTriggerPeriod() {
    SimpleTriggerInputDTO simpleTriggerInputDTO = minimalSimpleTrigger();
    simpleTriggerInputDTO.setEndDate(new Date());
    simpleTriggerInputDTO.setStartDate(DateUtils.addHoursToNow(1));
    return simpleTriggerInputDTO;
  }

}
