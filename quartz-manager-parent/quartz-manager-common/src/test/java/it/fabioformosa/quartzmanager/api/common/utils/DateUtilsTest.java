package it.fabioformosa.quartzmanager.api.common.utils;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

class DateUtilsTest {

  @Test
  void givenALocaleDatetime_whenTheConversionIsCalled_shouldGetADate(){
    LocalDateTime originalLocalDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
    Date date = DateUtils.fromLocalDateTimeToDate(originalLocalDateTime);
    LocalDateTime convertedLocalDateTime = DateUtils.fromDateToLocalDateTime(date);
    Assertions.assertThat(convertedLocalDateTime).isEqualTo(originalLocalDateTime);
  }

  @Test
  void givenALocalDatetime_whenTheAddHoursToNowIsCalled_shouldReturnAFutureDate(){
    Calendar calendar = Calendar.getInstance();
    Date futureDate = DateUtils.addHoursToNow(1);

    calendar.add(Calendar.HOUR_OF_DAY, 1);
    calendar.add(Calendar.MINUTE, -1);
    Date hourStartingAround = calendar.getTime();

    calendar.add(Calendar.HOUR_OF_DAY, 1);
    calendar.add(Calendar.MINUTE, 2);
    Date hourEndingAround = calendar.getTime();

    Assertions.assertThat(futureDate).isBetween(hourStartingAround, hourEndingAround);
  }

}
