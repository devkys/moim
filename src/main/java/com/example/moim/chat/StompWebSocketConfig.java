package com.example.moim.chat;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

@EnableWebSocketMessageBroker
@Configuration
public class StompWebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        // http를 통한 handshake와 통신을 당담할 endpoint 지정
        // http://localhost:8081/stomp/chat
        registry.addEndpoint("/stomp/chat")
                .setAllowedOrigins("http://localhost:5173")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // destination header가 /pub로 시작하는 stomp 메시지는
        // @MessageMapping 어노테이션으로 라우팅됨
        config.setApplicationDestinationPrefixes("/pub");

        // 수신과 브로드 캐스팅을 위해 내장 메시지 브로커 사용
        // destination header가 /sub로 시작하는 메시지를 브로커로 라우팅
        config.enableSimpleBroker("/sub");
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.setMessageSizeLimit(50*1024*1024);
    }
}
