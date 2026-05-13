package it.fabioformosa.quartzmanager.api.services;

import it.fabioformosa.quartzmanager.api.dto.CalendarDTO;
import it.fabioformosa.quartzmanager.api.dto.CalendarIncludedTimeDTO;
import it.fabioformosa.quartzmanager.api.dto.CalendarType;
import it.fabioformosa.quartzmanager.api.exceptions.CalendarNotFoundException;
import it.fabioformosa.quartzmanager.api.exceptions.ResourceConflictException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.quartz.Calendar;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.calendar.AnnualCalendar;
import org.quartz.impl.calendar.CronCalendar;
import org.quartz.impl.calendar.DailyCalendar;
import org.quartz.impl.calendar.HolidayCalendar;
import org.quartz.impl.calendar.MonthlyCalendar;
import org.quartz.impl.calendar.WeeklyCalendar;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

class CalendarServiceTest {

  @Mock
  private Scheduler scheduler;

  private CalendarService calendarService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    calendarService = new CalendarService(scheduler);
  }

  @Test
  void givenCalendarNames_whenCalendarsAreFetched_thenReturnsCalendarDtos() throws SchedulerException {
    Mockito.when(scheduler.getCalendarNames()).thenReturn(List.of("weekday"));
    WeeklyCalendar weeklyCalendar = new WeeklyCalendar();
    weeklyCalendar.setDayExcluded(java.util.Calendar.MONDAY, true);
    Mockito.when(scheduler.getCalendar("weekday")).thenReturn(weeklyCalendar);
    Mockito.when(scheduler.getTriggerKeys(any())).thenReturn(Set.of());

    List<CalendarDTO> calendars = calendarService.fetchCalendars();

    Assertions.assertThat(calendars).hasSize(1);
    Assertions.assertThat(calendars.get(0).getName()).isEqualTo("weekday");
    Assertions.assertThat(calendars.get(0).getType()).isEqualTo(CalendarType.WEEKLY);
    Assertions.assertThat(calendars.get(0).getExcludedDaysOfWeek()).contains(java.util.Calendar.MONDAY);
  }

  @Test
  void givenMissingCalendar_whenCalendarIsFetched_thenThrowsNotFound() throws SchedulerException {
    Mockito.when(scheduler.getCalendar("missing")).thenReturn(null);

    Assertions.assertThatThrownBy(() -> calendarService.getCalendar("missing"))
      .isInstanceOf(CalendarNotFoundException.class);
  }

  @Test
  void givenExistingCalendar_whenCalendarIsAdded_thenThrowsConflict() throws SchedulerException {
    Mockito.when(scheduler.getCalendar("existing")).thenReturn(new HolidayCalendar());

    CalendarDTO calendarDTO = CalendarDTO.builder().type(CalendarType.HOLIDAY).build();

    Assertions.assertThatThrownBy(() -> calendarService.addCalendar("existing", calendarDTO))
      .isInstanceOf(ResourceConflictException.class);
  }

  @Test
  void givenHolidayCalendar_whenCalendarIsAdded_thenStoresAndReturnsExcludedDates() throws SchedulerException, ParseException {
    Date excludedDate = new Date(86_400_000L);
    CalendarDTO calendarDTO = CalendarDTO.builder()
      .type(CalendarType.HOLIDAY)
      .description("holidays")
      .excludedDates(List.of(excludedDate))
      .build();
    Mockito.when(scheduler.getCalendar("holidays")).thenReturn(null);
    Mockito.when(scheduler.getTriggerKeys(any())).thenReturn(Set.of());
    ArgumentCaptor<Calendar> calendarCaptor = ArgumentCaptor.forClass(Calendar.class);

    CalendarDTO result = calendarService.addCalendar("holidays", calendarDTO);

    Mockito.verify(scheduler).addCalendar(eq("holidays"), calendarCaptor.capture(), eq(false), eq(false));
    Assertions.assertThat(calendarCaptor.getValue()).isInstanceOf(HolidayCalendar.class);
    Assertions.assertThat(result.getType()).isEqualTo(CalendarType.HOLIDAY);
    Assertions.assertThat(result.getDescription()).isEqualTo("holidays");
    Assertions.assertThat(result.getExcludedDates()).hasSize(1);
  }

  @Test
  void givenCronCalendar_whenCalendarIsAdded_thenStoresTimezone() throws SchedulerException, ParseException {
    CalendarDTO calendarDTO = CalendarDTO.builder()
      .type(CalendarType.CRON)
      .cronExpression("0 0 12 * * ?")
      .timeZone("UTC")
      .build();
    Mockito.when(scheduler.getCalendar("cron")).thenReturn(null);
    Mockito.when(scheduler.getTriggerKeys(any())).thenReturn(Set.of());
    ArgumentCaptor<Calendar> calendarCaptor = ArgumentCaptor.forClass(Calendar.class);

    CalendarDTO result = calendarService.addCalendar("cron", calendarDTO);

    Mockito.verify(scheduler).addCalendar(eq("cron"), calendarCaptor.capture(), eq(false), eq(false));
    Assertions.assertThat(calendarCaptor.getValue()).isInstanceOf(CronCalendar.class);
    Assertions.assertThat(result.getCronExpression()).isEqualTo("0 0 12 * * ?");
    Assertions.assertThat(result.getTimeZone()).isEqualTo("UTC");
  }

  @Test
  void givenDailyCalendar_whenCalendarIsAdded_thenStoresRange() throws SchedulerException, ParseException {
    CalendarDTO calendarDTO = CalendarDTO.builder()
      .type(CalendarType.DAILY)
      .rangeStartingTime("08:00:00")
      .rangeEndingTime("18:30:00")
      .invertTimeRange(true)
      .build();
    Mockito.when(scheduler.getCalendar("daily")).thenReturn(null);
    Mockito.when(scheduler.getTriggerKeys(any())).thenReturn(Set.of());

    CalendarDTO result = calendarService.addCalendar("daily", calendarDTO);

    Assertions.assertThat(result.getType()).isEqualTo(CalendarType.DAILY);
    Assertions.assertThat(result.getRangeStartingTime()).isEqualTo("08:00:00");
    Assertions.assertThat(result.getRangeEndingTime()).isEqualTo("18:30:00");
    Assertions.assertThat(result.getInvertTimeRange()).isTrue();
  }

  @Test
  void givenAnnualMonthlyAndWeeklyCalendars_whenFetched_thenCalendarSpecificFieldsAreMapped() throws SchedulerException {
    Date excludedDate = new Date(86_400_000L);
    AnnualCalendar annualCalendar = new AnnualCalendar();
    java.util.Calendar excludedDay = java.util.Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    excludedDay.setTime(excludedDate);
    annualCalendar.setDayExcluded(excludedDay, true);

    MonthlyCalendar monthlyCalendar = new MonthlyCalendar();
    monthlyCalendar.setDayExcluded(10, true);

    WeeklyCalendar weeklyCalendar = new WeeklyCalendar();
    weeklyCalendar.setDayExcluded(java.util.Calendar.FRIDAY, true);

    Mockito.when(scheduler.getTriggerKeys(any())).thenReturn(Set.of());
    Mockito.when(scheduler.getCalendar("annual")).thenReturn(annualCalendar);
    Mockito.when(scheduler.getCalendar("monthly")).thenReturn(monthlyCalendar);
    Mockito.when(scheduler.getCalendar("weekly")).thenReturn(weeklyCalendar);

    Assertions.assertThat(calendarService.getCalendar("annual").getExcludedDates()).hasSize(1);
    Assertions.assertThat(calendarService.getCalendar("monthly").getExcludedDaysOfMonth()).containsExactly(10);
    Assertions.assertThat(calendarService.getCalendar("weekly").getExcludedDaysOfWeek()).contains(java.util.Calendar.FRIDAY);
  }

  @Test
  void givenCalendarUsedByTrigger_whenCalendarIsFetched_thenTriggerKeysAreIncluded() throws SchedulerException {
    HolidayCalendar holidayCalendar = new HolidayCalendar();
    TriggerKey triggerKey = TriggerKey.triggerKey("trigger", "group");
    Trigger trigger = TriggerBuilder.newTrigger()
      .withIdentity(triggerKey)
      .modifiedByCalendar("holidays")
      .build();
    Mockito.when(scheduler.getCalendar("holidays")).thenReturn(holidayCalendar);
    Mockito.when(scheduler.getTriggerKeys(any())).thenReturn(Set.of(triggerKey));
    Mockito.when(scheduler.getTrigger(triggerKey)).thenReturn(trigger);

    CalendarDTO result = calendarService.getCalendar("holidays");

    Assertions.assertThat(result.getTriggerKeys()).hasSize(1);
    Assertions.assertThat(result.getTriggerKeys().get(0).getName()).isEqualTo("trigger");
    Assertions.assertThat(result.getTriggerKeys().get(0).getGroup()).isEqualTo("group");
  }

  @Test
  void givenCalendar_whenIncludedTimeIsTested_thenReturnsIncludedAndNextIncludedTime() throws SchedulerException {
    HolidayCalendar holidayCalendar = new HolidayCalendar();
    Date excludedDate = new Date(86_400_000L);
    holidayCalendar.addExcludedDate(excludedDate);
    Mockito.when(scheduler.getCalendar("holidays")).thenReturn(holidayCalendar);

    CalendarIncludedTimeDTO result = calendarService.testIncludedTime("holidays", CalendarIncludedTimeDTO.builder().time(excludedDate).build());

    Assertions.assertThat(result.getIncluded()).isFalse();
    Assertions.assertThat(result.getNextIncludedTime()).isAfter(excludedDate);
  }

  @Test
  void givenMissingCalendar_whenDeleted_thenThrowsNotFound() throws SchedulerException {
    Mockito.when(scheduler.deleteCalendar("missing")).thenReturn(false);

    Assertions.assertThatThrownBy(() -> calendarService.deleteCalendar("missing"))
      .isInstanceOf(CalendarNotFoundException.class);
  }

  @Test
  void givenExistingCalendar_whenUpdated_thenReplacesCalendar() throws SchedulerException, ParseException {
    Mockito.when(scheduler.getCalendar("monthly")).thenReturn(new MonthlyCalendar());
    Mockito.when(scheduler.getTriggerKeys(any())).thenReturn(Set.of());
    CalendarDTO calendarDTO = CalendarDTO.builder()
      .type(CalendarType.MONTHLY)
      .excludedDaysOfMonth(Set.of(3, 9))
      .build();

    CalendarDTO result = calendarService.updateCalendar("monthly", calendarDTO);

    Mockito.verify(scheduler).addCalendar(eq("monthly"), any(Calendar.class), eq(true), eq(true));
    Assertions.assertThat(result.getExcludedDaysOfMonth()).containsExactly(3, 9);
  }
}
