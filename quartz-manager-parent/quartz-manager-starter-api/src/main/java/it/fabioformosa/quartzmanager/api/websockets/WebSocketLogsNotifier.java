package it.fabioformosa.quartzmanager.api.websockets;

import it.fabioformosa.quartzmanager.api.jobs.entities.LogRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

@Component
public class WebSocketLogsNotifier implements WebhookSender<LogRecord> {

  public static final String TOPIC_LOGS = "/topic/logs";

  @Autowired
  private SimpMessageSendingOperations messagingTemplate;

  @Override
  public void send(LogRecord logRecord) {
    messagingTemplate.convertAndSend(TOPIC_LOGS, logRecord);
  }
}
