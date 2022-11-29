package it.fabioformosa.quartzmanager.api.jobs.entities;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * Log record produced by a job at the end of each run
 *
 * @author Fabio.Formosa
 *
 */
@Data
@ToString
public class LogRecord {

  public enum LogType {
    INFO, WARN, ERROR;
  }

  private Date date;
  private LogType type;

  private String message;
  private String threadName;

  public LogRecord(LogType type, String msg) {
    super();
    this.type = type;
    message = msg;
    date = new Date();
  }

}
