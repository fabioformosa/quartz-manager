package it.fabioformosa.quartzmanager.api.validators;

import it.fabioformosa.quartzmanager.api.dto.TriggerRepetitionDTO;
import it.fabioformosa.quartzmanager.api.dto.SimpleTriggerInputDTO;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ValidRepetitionValidatorTest {

  private ValidRepetitionValidator validRepetitionValidator = new ValidRepetitionValidator();

  @Test
  void givenACountAndIntervalSet_whenTheValidatorIsCalled_shouldReturnValid() {
    TriggerRepetitionDTO repetitionDTO = new SimpleTriggerInputDTO();
    repetitionDTO.setRepeatCount(10);
    repetitionDTO.setRepeatInterval(1000L);
    boolean valid = validRepetitionValidator.isValid(repetitionDTO, null);
    Assertions.assertThat(valid).isTrue();
  }

  @Test
  void givenACountAndIntervalUnSet_whenTheValidatorIsCalled_shouldReturnInValid() {
    TriggerRepetitionDTO repetitionDTO = new SimpleTriggerInputDTO();
    boolean valid = validRepetitionValidator.isValid(repetitionDTO, null);
    Assertions.assertThat(valid).isTrue();
  }

  @ParameterizedTest
  @CsvSource({"10, ", ",1000"})
  void givenACountAndIntervalNotSet_whenTheValidatorIsCalled_shouldReturnInValid(String repeatCountStr, String repeatIntervalStr) {
    Integer repeatCount = null;
    if (StringUtils.isNotBlank(repeatCountStr))
      repeatCount = Integer.valueOf(repeatCountStr);

    Long repeatInterval = null;
    if (StringUtils.isNotBlank(repeatIntervalStr))
      repeatInterval = Long.valueOf(repeatIntervalStr);

    TriggerRepetitionDTO repetitionDTO = new SimpleTriggerInputDTO();
    repetitionDTO.setRepeatInterval(repeatInterval);
    repetitionDTO.setRepeatCount(repeatCount);

    boolean valid = validRepetitionValidator.isValid(repetitionDTO, null);
    Assertions.assertThat(valid).isFalse();
  }


}
