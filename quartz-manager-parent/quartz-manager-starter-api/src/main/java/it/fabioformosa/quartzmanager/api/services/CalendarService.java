package it.fabioformosa.quartzmanager.api.services;

import it.fabioformosa.quartzmanager.api.dto.CalendarDTO;
import it.fabioformosa.quartzmanager.api.dto.CalendarIncludedTimeDTO;
import it.fabioformosa.quartzmanager.api.dto.CalendarType;
import it.fabioformosa.quartzmanager.api.dto.TriggerKeyDTO;
import it.fabioformosa.quartzmanager.api.exceptions.CalendarNotFoundException;
import it.fabioformosa.quartzmanager.api.exceptions.ResourceConflictException;
import org.quartz.Calendar;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.calendar.AnnualCalendar;
import org.quartz.impl.calendar.CronCalendar;
import org.quartz.impl.calendar.DailyCalendar;
import org.quartz.impl.calendar.HolidayCalendar;
import org.quartz.impl.calendar.MonthlyCalendar;
import org.quartz.impl.calendar.WeeklyCalendar;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

@Service
public class CalendarService {

  private final Scheduler scheduler;

  public CalendarService(@Qualifier("quartzManagerScheduler") Scheduler scheduler) {
    this.scheduler = scheduler;
  }

  public List<CalendarDTO> fetchCalendars() throws SchedulerException {
    return scheduler.getCalendarNames().stream()
      .map(this::getCalendarUnchecked)
      .toList();
  }

  public CalendarDTO getCalendar(String name) throws SchedulerException {
    Calendar calendar = scheduler.getCalendar(name);
    if (calendar == null)
      throw new CalendarNotFoundException(name);
    return toDTO(name, calendar);
  }

  public CalendarDTO addCalendar(String name, CalendarDTO calendarDTO) throws SchedulerException, ParseException {
    if (scheduler.getCalendar(name) != null)
      throw new ResourceConflictException("Calendar " + name + " already exists");
    Calendar calendar = buildCalendar(calendarDTO);
    scheduler.addCalendar(name, calendar, false, false);
    return toDTO(name, calendar);
  }

  public CalendarDTO updateCalendar(String name, CalendarDTO calendarDTO) throws SchedulerException, ParseException {
    if (scheduler.getCalendar(name) == null)
      throw new CalendarNotFoundException(name);
    Calendar calendar = buildCalendar(calendarDTO);
    scheduler.addCalendar(name, calendar, true, true);
    return toDTO(name, calendar);
  }

  public void deleteCalendar(String name) throws SchedulerException {
    if (!scheduler.deleteCalendar(name))
      throw new CalendarNotFoundException(name);
  }

  public CalendarIncludedTimeDTO testIncludedTime(String name, CalendarIncludedTimeDTO input) throws SchedulerException {
    Calendar calendar = scheduler.getCalendar(name);
    if (calendar == null)
      throw new CalendarNotFoundException(name);
    long timestamp = input.getTime().getTime();
    return CalendarIncludedTimeDTO.builder()
      .time(input.getTime())
      .included(calendar.isTimeIncluded(timestamp))
      .nextIncludedTime(new Date(calendar.getNextIncludedTime(timestamp)))
      .build();
  }

  private CalendarDTO getCalendarUnchecked(String name) {
    try {
      return getCalendar(name);
    } catch (SchedulerException ex) {
      throw new IllegalStateException(ex);
    }
  }

  private Calendar buildCalendar(CalendarDTO calendarDTO) throws ParseException {
    Calendar calendar = switch (calendarDTO.getType()) {
      case ANNUAL -> buildAnnualCalendar(calendarDTO);
      case CRON -> buildCronCalendar(calendarDTO);
      case DAILY -> buildDailyCalendar(calendarDTO);
      case HOLIDAY -> buildHolidayCalendar(calendarDTO);
      case MONTHLY -> buildMonthlyCalendar(calendarDTO);
      case WEEKLY -> buildWeeklyCalendar(calendarDTO);
    };
    calendar.setDescription(calendarDTO.getDescription());
    return calendar;
  }

  private AnnualCalendar buildAnnualCalendar(CalendarDTO calendarDTO) {
    AnnualCalendar calendar = new AnnualCalendar();
    for (Date excludedDate : calendarDTO.getExcludedDatesOrEmpty()) {
      java.util.Calendar excludedDay = java.util.Calendar.getInstance();
      excludedDay.setTime(excludedDate);
      calendar.setDayExcluded(excludedDay, true);
    }
    return calendar;
  }

  private CronCalendar buildCronCalendar(CalendarDTO calendarDTO) throws ParseException {
    CronCalendar calendar = new CronCalendar(calendarDTO.getCronExpression());
    if (calendarDTO.getTimeZone() != null && !calendarDTO.getTimeZone().isBlank())
      calendar.setTimeZone(TimeZone.getTimeZone(calendarDTO.getTimeZone()));
    return calendar;
  }

  private DailyCalendar buildDailyCalendar(CalendarDTO calendarDTO) {
    DailyCalendar calendar = new DailyCalendar(calendarDTO.getRangeStartingTime(), calendarDTO.getRangeEndingTime());
    calendar.setInvertTimeRange(Boolean.TRUE.equals(calendarDTO.getInvertTimeRange()));
    return calendar;
  }

  private HolidayCalendar buildHolidayCalendar(CalendarDTO calendarDTO) {
    HolidayCalendar calendar = new HolidayCalendar();
    for (Date excludedDate : calendarDTO.getExcludedDatesOrEmpty())
      calendar.addExcludedDate(excludedDate);
    return calendar;
  }

  private MonthlyCalendar buildMonthlyCalendar(CalendarDTO calendarDTO) {
    MonthlyCalendar calendar = new MonthlyCalendar();
    for (Integer day : calendarDTO.getExcludedDaysOfMonthOrEmpty())
      calendar.setDayExcluded(day, true);
    return calendar;
  }

  private WeeklyCalendar buildWeeklyCalendar(CalendarDTO calendarDTO) {
    WeeklyCalendar calendar = new WeeklyCalendar();
    for (Integer day : calendarDTO.getExcludedDaysOfWeekOrEmpty())
      calendar.setDayExcluded(day, true);
    return calendar;
  }

  private CalendarDTO toDTO(String name, Calendar calendar) throws SchedulerException {
    CalendarDTO calendarDTO = CalendarDTO.builder()
      .name(name)
      .description(calendar.getDescription())
      .triggerKeys(findTriggerKeys(name))
      .build();

    if (calendar instanceof AnnualCalendar annualCalendar)
      enrichAnnualCalendar(calendarDTO, annualCalendar);
    else if (calendar instanceof CronCalendar cronCalendar)
      enrichCronCalendar(calendarDTO, cronCalendar);
    else if (calendar instanceof DailyCalendar dailyCalendar)
      enrichDailyCalendar(calendarDTO, dailyCalendar);
    else if (calendar instanceof HolidayCalendar holidayCalendar)
      enrichHolidayCalendar(calendarDTO, holidayCalendar);
    else if (calendar instanceof MonthlyCalendar monthlyCalendar)
      enrichMonthlyCalendar(calendarDTO, monthlyCalendar);
    else if (calendar instanceof WeeklyCalendar weeklyCalendar)
      enrichWeeklyCalendar(calendarDTO, weeklyCalendar);
    return calendarDTO;
  }

  private void enrichAnnualCalendar(CalendarDTO calendarDTO, AnnualCalendar calendar) {
    calendarDTO.setType(CalendarType.ANNUAL);
    calendarDTO.setExcludedDates(calendar.getDaysExcluded().stream().map(java.util.Calendar::getTime).toList());
  }

  private void enrichCronCalendar(CalendarDTO calendarDTO, CronCalendar calendar) {
    calendarDTO.setType(CalendarType.CRON);
    calendarDTO.setCronExpression(calendar.getCronExpression().getCronExpression());
    calendarDTO.setTimeZone(calendar.getTimeZone().getID());
  }

  private void enrichDailyCalendar(CalendarDTO calendarDTO, DailyCalendar calendar) {
    calendarDTO.setType(CalendarType.DAILY);
    long now = System.currentTimeMillis();
    calendarDTO.setRangeStartingTime(formatTime(calendar.getTimeRangeStartingTimeInMillis(now)));
    calendarDTO.setRangeEndingTime(formatTime(calendar.getTimeRangeEndingTimeInMillis(now)));
    calendarDTO.setInvertTimeRange(calendar.getInvertTimeRange());
  }

  private void enrichHolidayCalendar(CalendarDTO calendarDTO, HolidayCalendar calendar) {
    calendarDTO.setType(CalendarType.HOLIDAY);
    calendarDTO.setExcludedDates(new ArrayList<>(calendar.getExcludedDates()));
  }

  private void enrichMonthlyCalendar(CalendarDTO calendarDTO, MonthlyCalendar calendar) {
    calendarDTO.setType(CalendarType.MONTHLY);
    Set<Integer> excludedDays = new TreeSet<>();
    for (int day = 1; day <= 31; day++) {
      if (calendar.isDayExcluded(day))
        excludedDays.add(day);
    }
    calendarDTO.setExcludedDaysOfMonth(excludedDays);
  }

  private void enrichWeeklyCalendar(CalendarDTO calendarDTO, WeeklyCalendar calendar) {
    calendarDTO.setType(CalendarType.WEEKLY);
    Set<Integer> excludedDays = new TreeSet<>();
    for (int day = java.util.Calendar.SUNDAY; day <= java.util.Calendar.SATURDAY; day++) {
      if (calendar.isDayExcluded(day))
        excludedDays.add(day);
    }
    calendarDTO.setExcludedDaysOfWeek(excludedDays);
  }

  private List<TriggerKeyDTO> findTriggerKeys(String calendarName) throws SchedulerException {
    List<TriggerKeyDTO> triggerKeys = new ArrayList<>();
    for (TriggerKey triggerKey : scheduler.getTriggerKeys(GroupMatcher.anyTriggerGroup())) {
      Trigger trigger = scheduler.getTrigger(triggerKey);
      if (trigger != null && calendarName.equals(trigger.getCalendarName()))
        triggerKeys.add(TriggerKeyDTO.builder().name(triggerKey.getName()).group(triggerKey.getGroup()).build());
    }
    return triggerKeys;
  }

  private String formatTime(long timeInMillis) {
    java.util.Calendar calendar = java.util.Calendar.getInstance();
    calendar.setTimeInMillis(timeInMillis);
    return String.format("%02d:%02d:%02d", calendar.get(java.util.Calendar.HOUR_OF_DAY), calendar.get(java.util.Calendar.MINUTE), calendar.get(java.util.Calendar.SECOND));
  }
}
