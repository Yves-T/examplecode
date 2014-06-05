package spring;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // enables a simple memory-based message broker to carry messages back to the client
        // on destinations prefixed with "/topic"
        config.enableSimpleBroker("/topic");
        // /app is the application prefix where spring MVC will listen for browser requests
        config.setApplicationDestinationPrefixes("/app");
    }


    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // /hello is the websocket endpoint
        registry.addEndpoint("/hello").withSockJS();
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        // The default is backed by thread pool size of 1
        // This is to low for production use
        registration.taskExecutor().corePoolSize(4).maxPoolSize(10);
    }
}