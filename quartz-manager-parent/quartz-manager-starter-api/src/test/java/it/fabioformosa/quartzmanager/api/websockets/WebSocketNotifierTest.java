package it.fabioformosa.quartzmanager.api.websockets;

import it.fabioformosa.quartzmanager.api.dto.TriggerFiredBundleDTO;
import it.fabioformosa.quartzmanager.api.jobs.entities.LogRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

import static org.mockito.MockitoAnnotations.openMocks;

class WebSocketNotifierTest {

  @InjectMocks
  private WebSocketLogsNotifier webSocketLogsNotifier;

  @InjectMocks
  private WebSocketProgressNotifier webSocketProgressNotifier;

  @Mock
  private SimpMessageSendingOperations messagingTemplate;

  @BeforeEach
  void setUp() {
    openMocks(this);
  }

  @Test
  void givenATriggerName_whenALogIsSent_thenShouldSendItToTheTriggerLogsTopic() {
    LogRecord logRecord = new LogRecord(LogRecord.LogType.INFO, "Hello!");

    webSocketLogsNotifier.send("trigger-1", logRecord);

    Mockito.verify(messagingTemplate).convertAndSend("/topic/logs/trigger-1", logRecord);
  }

  @Test
  void givenATriggerName_whenProgressIsSent_thenShouldSendItToTheTriggerProgressTopic() {
    TriggerFiredBundleDTO triggerFiredBundleDTO = new TriggerFiredBundleDTO();

    webSocketProgressNotifier.send("trigger-1", triggerFiredBundleDTO);

    Mockito.verify(messagingTemplate).convertAndSend("/topic/progress/trigger-1", triggerFiredBundleDTO);
  }

}
