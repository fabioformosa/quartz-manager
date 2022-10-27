package it.fabioformosa.quartzmanager.api.common.utils;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

class DateUtilsTest {

  @Test
  public void givenALocaleDatetime_whenTheConversionIsCalled_shouldGetADate(){
    LocalDateTime originalLocalDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
    Date date = DateUtils.fromLocalDateTimeToDate(originalLocalDateTime);
    LocalDateTime convertedLocalDateTime = DateUtils.fromDateToLocalDateTime(date);
    Assertions.assertThat(convertedLocalDateTime).isEqualTo(originalLocalDateTime);
  }

}
