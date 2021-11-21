package com.example.demowebsocketjava.controller;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Controller
public class SampleWebsocketController {

    @MessageMapping("/chat")
    @SendTo("/topic/greetings")
    SampleResponse sampleAPI (SampleRequest request) throws Exception{
        Assert.isTrue(Character.isUpperCase(request.value().charAt(0)), ()->"The name must start with a capital letter!");
        Thread.sleep(1_000);
        System.out.println("message : " + request.value());
        return new SampleResponse("Hello, " + request.value() + "!!");
    }

    @MessageExceptionHandler
    @SendTo("/topic/errors")
    public String hadndleException(IllegalArgumentException e) {
        var message = ("an exception occured!!"  + NestedExceptionUtils.getMostSpecificCause(e));
        System.out.println(message);
        return message;
    }


}


record SampleRequest (String value){}
record SampleResponse (String message){}

@Configuration
@EnableWebSocketMessageBroker
class SampleWebsocketConfiguration implements WebSocketMessageBrokerConfigurer {
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chat").withSockJS();
    }
}