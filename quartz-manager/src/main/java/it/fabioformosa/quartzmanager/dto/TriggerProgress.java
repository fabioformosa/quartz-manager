package it.fabioformosa.quartzmanager.dto;

import java.util.Date;

public class TriggerProgress {

	private int timesTriggered;

	private int repeatCount;

	private Date finalFireTime;

	private Date nextFireTime;

	private Date previousFireTime;

	private String jobKey;

	private String jobClass;

	public Date getFinalFireTime() {
		return finalFireTime;
	}

	public String getJobClass() {
		return jobClass;
	}

	public String getJobKey() {
		return jobKey;
	}

	public Date getNextFireTime() {
		return nextFireTime;
	}

	public int getPercentage() {
		if (this.repeatCount <= 0)
			return -1;
		return Math.round((float) timesTriggered / (float) this.repeatCount
				* 100);
	}

	public Date getPreviousFireTime() {
		return previousFireTime;
	}

	public int getRepeatCount() {
		return repeatCount;
	}

	public int getTimesTriggered() {
		return timesTriggered;
	}

	public void setFinalFireTime(Date finalFireTime) {
		this.finalFireTime = finalFireTime;
	}

	public void setJobClass(String jobClass) {
		this.jobClass = jobClass;
	}

	public void setJobKey(String jobKey) {
		this.jobKey = jobKey;
	}

	public void setNextFireTime(Date nextFireTime) {
		this.nextFireTime = nextFireTime;
	}

	public void setPreviousFireTime(Date previousFireTime) {
		this.previousFireTime = previousFireTime;
	}

	public void setRepeatCount(int repeatCount) {
		this.repeatCount = repeatCount;
	}

	public void setTimesTriggered(int timesTriggered) {
		this.timesTriggered = timesTriggered;
	}

}
