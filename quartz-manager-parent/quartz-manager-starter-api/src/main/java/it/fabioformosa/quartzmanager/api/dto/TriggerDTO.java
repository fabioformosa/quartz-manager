package it.fabioformosa.quartzmanager.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.quartz.JobDataMap;

import java.util.Date;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
public class TriggerDTO {
  private TriggerKeyDTO triggerKeyDTO;
  private int priority;
  private Date startTime;
  private String description;
  private Date endTime;
  private Date finalFireTime;
  private int misfireInstruction;
  private Date nextFireTime;
  private Date previousFireTime;
  private String type;
  private String state;
  private String calendarName;
  private JobKeyDTO jobKeyDTO;
  private JobDetailDTO jobDetailDTO;
  private boolean mayFireAgain;
  private JobDataMap jobDataMap;
  private String cronExpression;
  private String timeZone;
  private Long repeatInterval;
  private Integer repeatCount;
  private String repeatIntervalUnit;
  private String startTimeOfDay;
  private String endTimeOfDay;
  private Set<Integer> daysOfWeek;
  private Boolean preserveHourOfDayAcrossDaylightSavings;
  private Boolean skipDayIfHourDoesNotExist;
}
