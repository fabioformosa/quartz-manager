package it.fabioformosa.quartzmanager.controllers;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import static it.fabioformosa.quartzmanager.common.config.QuartzManagerPaths.QUARTZ_MANAGER_BASE_CONTEXT_PATH;

@Controller
public class WebsocketController {

    @MessageMapping({ QUARTZ_MANAGER_BASE_CONTEXT_PATH + "/logs", QUARTZ_MANAGER_BASE_CONTEXT_PATH + "/progress" })
    @SendTo("/topic/logs")
    public String subscribe() {
        return "subscribed";
    }

}
