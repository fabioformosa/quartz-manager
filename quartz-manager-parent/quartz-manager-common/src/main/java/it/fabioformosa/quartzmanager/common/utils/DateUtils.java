package it.fabioformosa.quartzmanager.common.utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class DateUtils {

  static public Date fromLocaleDateTimeToDate(LocalDateTime localDateTime){
    return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
  }

  static public Date getHoursFromNow(long hours){
    return DateUtils.fromLocaleDateTimeToDate(LocalDateTime.now().plus(Duration.ofHours(hours)));
  }

}
