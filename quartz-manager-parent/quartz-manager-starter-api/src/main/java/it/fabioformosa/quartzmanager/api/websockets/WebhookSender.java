package it.fabioformosa.quartzmanager.api.websockets;

/**
 *
 * Notify the progress of the trigger to all consumers
 *
 * @author Fabio Formosa
 *
 */
public interface WebhookSender<T> {

	void send(String triggerName, T message);

}
