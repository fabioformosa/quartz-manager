package it.fabioformosa.quartzmanager.aspects;

import org.quartz.SchedulerException;

/**
 *
 * Notify the progress of the trigger to all consumers
 *
 * @author Fabio Formosa
 *
 */
public interface ProgressNotifier {

	void send() throws SchedulerException;

}
