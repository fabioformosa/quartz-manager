package it.fabioformosa.quartzmanager.scheduler;

import org.quartz.Trigger;

public interface TriggerMonitor {

	void setTrigger(Trigger trigger);

	Trigger getTrigger();

}
