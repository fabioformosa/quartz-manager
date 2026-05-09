package it.fabioformosa.quartzmanager.api.configuration;

import it.fabioformosa.quartzmanager.api.common.config.QuartzManagerPaths;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@ComponentScan(basePackages = {"it.fabioformosa.quartzmanager.api.websockets"})
@EnableWebSocketMessageBroker
public class WebsocketConfig implements WebSocketMessageBrokerConfigurer {

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    config.enableSimpleBroker("/topic"); //enable a simple memory-based message broker
                                                        // on destinations prefixed with /topic
    config.setApplicationDestinationPrefixes("/job"); // it designates the prefix for messages
                                                      // that are bound for methods annotated with @MessageMapping
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint(QuartzManagerPaths.QUARTZ_MANAGER_BASE_CONTEXT_PATH + "/logs").setAllowedOrigins("/**").withSockJS();
    registry.addEndpoint(QuartzManagerPaths.QUARTZ_MANAGER_BASE_CONTEXT_PATH + "/progress").setAllowedOrigins("/**").withSockJS();
  }

}
