package it.fabioformosa.quartzmanager.websockets;

/**
 *
 * Notify the progress of the trigger to all consumers
 *
 * @author Fabio Formosa
 *
 */
public interface WebhookSender<T> {

	void send(T message);

}
