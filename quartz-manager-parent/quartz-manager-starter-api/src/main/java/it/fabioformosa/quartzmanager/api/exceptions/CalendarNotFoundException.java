package it.fabioformosa.quartzmanager.api.exceptions;

public class CalendarNotFoundException extends RuntimeException {
  public CalendarNotFoundException(String name) {
    super("Calendar " + name + " not found!");
  }
}
