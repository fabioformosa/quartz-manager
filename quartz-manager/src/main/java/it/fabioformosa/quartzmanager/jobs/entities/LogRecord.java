package it.fabioformosa.quartzmanager.jobs.entities;

public class LogRecord {

	public enum LogType {
		INFO, ERROR;
	}

	private LogType type;
	private String message;

	public LogRecord(LogType type, String msg) {
		super();
		this.type = type;
		message = msg;
	}

	public String getMessage() {
		return message;
	}

	public LogType getType() {
		return type;
	}

	public void setMessage(String msg) {
		message = msg;
	}

	public void setType(LogType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "LogRecord [type=" + type + ", message=" + message + "]";
	}

}
