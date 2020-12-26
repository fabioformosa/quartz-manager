package it.fabioformosa.quartzmanager.controllers;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebsocketController {

    @MessageMapping({ "/quartz-manager/logs", "/quartz-manager/progress" })
    @SendTo("/topic/logs")
    public String subscribe() throws Exception {
        return "subscribed";
    }

}
