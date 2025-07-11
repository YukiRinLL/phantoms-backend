//package com.phantoms.phantomsbackend.common.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.socket.TextMessage;
//import org.springframework.web.socket.WebSocketSession;
//import org.springframework.web.socket.config.annotation.EnableWebSocket;
//import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
//import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
//import org.springframework.web.socket.handler.TextWebSocketHandler;
//
//@Configuration
//@EnableWebSocket
//public class NativeWsConfig implements WebSocketConfigurer {
//    @Override
//    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//        registry.addHandler(new TextWebSocketHandler() {
//            @Override
//            protected void handleTextMessage(WebSocketSession session,
//                                             TextMessage message) throws Exception {
//                session.sendMessage(new TextMessage("Echo: " + message.getPayload()));
//            }
//        }, "/ws");
//    }
//}