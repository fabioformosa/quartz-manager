package it.fabioformosa.quartzmanager.api.controllers;

import it.fabioformosa.quartzmanager.api.QuartManagerApplicationTests;
import it.fabioformosa.quartzmanager.api.controllers.utils.TestUtils;
import it.fabioformosa.quartzmanager.api.dto.CalendarDTO;
import it.fabioformosa.quartzmanager.api.dto.CalendarIncludedTimeDTO;
import it.fabioformosa.quartzmanager.api.dto.CalendarType;
import it.fabioformosa.quartzmanager.api.services.CalendarService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.mockito.ArgumentMatchers.any;

@ContextConfiguration(classes = {QuartManagerApplicationTests.class})
@WebMvcTest(controllers = CalendarController.class, properties = {
  "quartz-manager.jobClassPackages=it.fabioformosa.quartzmanager.jobs"
})
class CalendarControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private CalendarService calendarService;

  @AfterEach
  void cleanUp(){
    Mockito.reset(calendarService);
  }

  @Test
  void whenListCalendarsIsCalled_thenCalendarsAreReturned() throws Exception {
    List<CalendarDTO> calendars = List.of(CalendarDTO.builder().name("weekends").type(CalendarType.WEEKLY).excludedDaysOfWeek(Set.of(1, 7)).build());
    Mockito.when(calendarService.fetchCalendars()).thenReturn(calendars);

    mockMvc.perform(get(CalendarController.CALENDAR_CONTROLLER_BASE_URL).contentType(MediaType.APPLICATION_JSON))
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andExpect(MockMvcResultMatchers.content().json(TestUtils.toJson(calendars)));
  }

  @Test
  void whenGetCalendarIsCalled_thenCalendarIsReturned() throws Exception {
    CalendarDTO calendarDTO = CalendarDTO.builder().name("cron").type(CalendarType.CRON).cronExpression("0 0 0 ? * SAT,SUN").build();
    Mockito.when(calendarService.getCalendar("cron")).thenReturn(calendarDTO);

    mockMvc.perform(get(CalendarController.CALENDAR_CONTROLLER_BASE_URL + "/cron").contentType(MediaType.APPLICATION_JSON))
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andExpect(MockMvcResultMatchers.content().json(TestUtils.toJson(calendarDTO)));
  }

  @Test
  void givenACalendarDTO_whenPosted_thenCalendarIsCreated() throws Exception {
    CalendarDTO calendarDTO = CalendarDTO.builder().name("holidays").type(CalendarType.HOLIDAY).excludedDates(List.of(new Date())).build();
    Mockito.when(calendarService.addCalendar(Mockito.eq("holidays"), any())).thenReturn(calendarDTO);

    mockMvc.perform(post(CalendarController.CALENDAR_CONTROLLER_BASE_URL + "/holidays")
        .contentType(MediaType.APPLICATION_JSON)
        .content(TestUtils.toJson(calendarDTO)))
      .andExpect(MockMvcResultMatchers.status().isCreated())
      .andExpect(MockMvcResultMatchers.content().json(TestUtils.toJson(calendarDTO)));
  }

  @Test
  void givenACalendarDTO_whenPut_thenCalendarIsUpdated() throws Exception {
    CalendarDTO calendarDTO = CalendarDTO.builder().name("month-end").type(CalendarType.MONTHLY).excludedDaysOfMonth(Set.of(31)).build();
    Mockito.when(calendarService.updateCalendar("month-end", calendarDTO)).thenReturn(calendarDTO);

    mockMvc.perform(put(CalendarController.CALENDAR_CONTROLLER_BASE_URL + "/month-end")
        .contentType(MediaType.APPLICATION_JSON)
        .content(TestUtils.toJson(calendarDTO)))
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andExpect(MockMvcResultMatchers.content().json(TestUtils.toJson(calendarDTO)));
  }

  @Test
  void whenDeleteCalendarIsCalled_thenNoContentIsReturned() throws Exception {
    mockMvc.perform(delete(CalendarController.CALENDAR_CONTROLLER_BASE_URL + "/weekends").contentType(MediaType.APPLICATION_JSON))
      .andExpect(MockMvcResultMatchers.status().isNoContent());

    Mockito.verify(calendarService).deleteCalendar("weekends");
  }

  @Test
  void whenIncludedTimeIsTested_thenResultIsReturned() throws Exception {
    CalendarIncludedTimeDTO input = CalendarIncludedTimeDTO.builder().time(new Date()).build();
    CalendarIncludedTimeDTO result = CalendarIncludedTimeDTO.builder().time(input.getTime()).included(true).nextIncludedTime(input.getTime()).build();
    Mockito.when(calendarService.testIncludedTime(Mockito.eq("weekends"), any())).thenReturn(result);

    mockMvc.perform(post(CalendarController.CALENDAR_CONTROLLER_BASE_URL + "/weekends/included-time-test")
        .contentType(MediaType.APPLICATION_JSON)
        .content(TestUtils.toJson(input)))
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andExpect(MockMvcResultMatchers.content().json(TestUtils.toJson(result)));
  }
}
