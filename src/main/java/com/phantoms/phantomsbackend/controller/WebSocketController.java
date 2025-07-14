package com.phantoms.phantomsbackend.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    /**
     * 客户端发送 /app/hello，服务器处理后再广播到 /topic/greetings
     */
    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Greeting greeting(HelloMessage message) throws Exception {
        Thread.sleep(1000); // 模拟延迟
        return new Greeting("Hello, " + message.getName() + "!");
    }

    /* ====== 两个 DTO 必须声明为 static ====== */

    public static class HelloMessage {
        private String name;

        public HelloMessage() {}
        public HelloMessage(String name) { this.name = name; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    public static class Greeting {
        private String content;

        public Greeting() {}
        public Greeting(String content) { this.content = content; }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }
}