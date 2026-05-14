package it.fabioformosa.quartzmanager.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import it.fabioformosa.quartzmanager.api.validators.JobTargetDTO;
import it.fabioformosa.quartzmanager.api.validators.ValidJobTarget;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;
import java.util.Set;

@Builder
@ValidJobTarget
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TriggerInputDTO implements JobTargetDTO {
  @NotNull
  private TriggerType triggerType;

  @Nullable
  private String jobClass;

  @Nullable
  private JobKeyDTO jobKey;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date startDate;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date endDate;

  private String description;
  private Integer priority;
  private String calendarName;
  private String misfireInstruction;

  @Nullable
  private Map<String, ?> jobDataMap;

  private Integer repeatCount;

  @Positive
  private Long repeatInterval;

  private String repeatIntervalUnit;
  private String cronExpression;
  private String timeZone;
  private String startTimeOfDay;
  private String endTimeOfDay;
  private Set<Integer> daysOfWeek;
  private Boolean preserveHourOfDayAcrossDaylightSavings;
  private Boolean skipDayIfHourDoesNotExist;
}
