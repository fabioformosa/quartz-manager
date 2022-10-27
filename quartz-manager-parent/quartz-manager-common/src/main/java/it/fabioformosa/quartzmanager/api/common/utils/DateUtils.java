package it.fabioformosa.quartzmanager.api.common.utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class DateUtils {

  static public Date fromLocalDateTimeToDate(LocalDateTime localDateTime){
    return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant().truncatedTo(ChronoUnit.MILLIS));
  }

  static public LocalDateTime fromDateToLocalDateTime(Date date) {
    return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().truncatedTo(ChronoUnit.MILLIS);
  }

  static public Date addHoursToNow(long hours){
    return DateUtils.fromLocalDateTimeToDate(LocalDateTime.now().plus(Duration.ofHours(hours)));
  }

}
