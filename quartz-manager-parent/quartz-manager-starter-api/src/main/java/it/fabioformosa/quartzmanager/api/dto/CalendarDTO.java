package it.fabioformosa.quartzmanager.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CalendarDTO {
  private String name;

  @NotNull
  private CalendarType type;

  private String description;
  private String cronExpression;
  private String timeZone;
  private String rangeStartingTime;
  private String rangeEndingTime;
  private Boolean invertTimeRange;
  private Set<Integer> excludedDaysOfWeek;
  private Set<Integer> excludedDaysOfMonth;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private List<Date> excludedDates;

  private List<TriggerKeyDTO> triggerKeys;

  public List<Date> getExcludedDatesOrEmpty() {
    return excludedDates == null ? Collections.emptyList() : excludedDates;
  }

  public Set<Integer> getExcludedDaysOfWeekOrEmpty() {
    return excludedDaysOfWeek == null ? Collections.emptySet() : excludedDaysOfWeek;
  }

  public Set<Integer> getExcludedDaysOfMonthOrEmpty() {
    return excludedDaysOfMonth == null ? Collections.emptySet() : excludedDaysOfMonth;
  }
}
