package it.fabioformosa.quartzmanager.api.controllers;

import it.fabioformosa.quartzmanager.api.common.config.QuartzManagerPaths;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebsocketController {

    @MessageMapping({
      QuartzManagerPaths.QUARTZ_MANAGER_BASE_CONTEXT_PATH + "/logs",
      QuartzManagerPaths.QUARTZ_MANAGER_BASE_CONTEXT_PATH + "/progress"
    })
    @SendTo("/topic/logs")
    public String subscribe() {
        return "subscribed";
    }

}
