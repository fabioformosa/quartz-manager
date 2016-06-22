package it.fabioformosa.quartzmanager.jobs.entities;

import java.util.Date;

public class LogRecord {

	public enum LogType {
		INFO, WARN, ERROR;
	}

	private Date date;
	private LogType type;

	private String message;

	public LogRecord(LogType type, String msg) {
		super();
		this.type = type;
		message = msg;
		date = new Date();
	}

	public Date getDate() {
		return date;
	}

	public String getMessage() {
		return message;
	}

	public LogType getType() {
		return type;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setMessage(String msg) {
		message = msg;
	}

	public void setType(LogType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "LogRecord [date=" + date + ", type=" + type + ", message="
				+ message + "]";
	}

}
