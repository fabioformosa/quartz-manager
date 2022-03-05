package it.fabioformosa.quartzmanager.controllers.utils;

import it.fabioformosa.quartzmanager.dto.SimpleTriggerInputDTO;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class InvalidSimpleTriggerCommandDTOProvider implements ArgumentsProvider {
  @Override
  public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
    return Stream.of(
      Arguments.of(SimpleTriggerInputDTO.builder().build()),
      Arguments.of(SimpleTriggerInputDTO.builder().repeatCount(1).build()),
      Arguments.of(SimpleTriggerInputDTO.builder().repeatInterval(1L).build())
    );
  }
}
