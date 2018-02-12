package it.fabioformosa.quartzmanager.dto;

public class SchedulerConfigParam {

	public long triggerPerDay;
	public int maxCount;

	public int getMaxCount() {
		return maxCount;
	}

	public long getTriggerPerDay() {
		return triggerPerDay;
	}

	public void setMaxCount(int maxCount) {
		this.maxCount = maxCount;
	}

	public void setTriggerPerDay(long triggerPerDay) {
		this.triggerPerDay = triggerPerDay;
	}

	@Override
	public String toString() {
		return "SchedulerConfigParam [triggerPerDay=" + triggerPerDay
				+ ", maxCount=" + maxCount + "]";
	}

}
