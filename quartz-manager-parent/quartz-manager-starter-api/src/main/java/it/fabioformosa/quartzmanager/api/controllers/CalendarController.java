package it.fabioformosa.quartzmanager.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.fabioformosa.quartzmanager.api.dto.CalendarDTO;
import it.fabioformosa.quartzmanager.api.dto.CalendarIncludedTimeDTO;
import it.fabioformosa.quartzmanager.api.services.CalendarService;
import jakarta.validation.Valid;
import org.quartz.SchedulerException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.List;

import static it.fabioformosa.quartzmanager.api.common.config.OpenAPIConfigConsts.QUARTZ_MANAGER_SEC_OAS_SCHEMA;
import static it.fabioformosa.quartzmanager.api.common.config.QuartzManagerPaths.QUARTZ_MANAGER_BASE_CONTEXT_PATH;

@RequestMapping(CalendarController.CALENDAR_CONTROLLER_BASE_URL)
@SecurityRequirement(name = QUARTZ_MANAGER_SEC_OAS_SCHEMA)
@RestController
public class CalendarController {

  protected static final String CALENDAR_CONTROLLER_BASE_URL = QUARTZ_MANAGER_BASE_CONTEXT_PATH + "/calendars";

  private final CalendarService calendarService;

  public CalendarController(CalendarService calendarService) {
    this.calendarService = calendarService;
  }

  @GetMapping
  @Operation(summary = "Get a list of calendars")
  public List<CalendarDTO> listCalendars() throws SchedulerException {
    return calendarService.fetchCalendars();
  }

  @GetMapping("/{name}")
  @Operation(summary = "Get calendar details")
  public CalendarDTO getCalendar(@PathVariable String name) throws SchedulerException {
    return calendarService.getCalendar(name);
  }

  @PostMapping("/{name}")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Create a calendar")
  public CalendarDTO postCalendar(@PathVariable String name, @Valid @RequestBody CalendarDTO calendarDTO) throws SchedulerException, ParseException {
    return calendarService.addCalendar(name, calendarDTO);
  }

  @PutMapping("/{name}")
  @Operation(summary = "Update a calendar")
  public CalendarDTO putCalendar(@PathVariable String name, @Valid @RequestBody CalendarDTO calendarDTO) throws SchedulerException, ParseException {
    return calendarService.updateCalendar(name, calendarDTO);
  }

  @DeleteMapping("/{name}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Delete a calendar")
  public void deleteCalendar(@PathVariable String name) throws SchedulerException {
    calendarService.deleteCalendar(name);
  }

  @PostMapping("/{name}/included-time-test")
  @Operation(summary = "Test if a time is included by a calendar")
  public CalendarIncludedTimeDTO testIncludedTime(@PathVariable String name, @Valid @RequestBody CalendarIncludedTimeDTO input) throws SchedulerException {
    return calendarService.testIncludedTime(name, input);
  }
}
