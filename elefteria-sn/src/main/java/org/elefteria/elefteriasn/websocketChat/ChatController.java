package org.elefteria.elefteriasn.websocketChat;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    @MessageMapping("/public-message")
    @SendTo("/public-chat/messages")
    public WebSocketMessage publicChatMessage(@Payload WebSocketMessage message){

        return message;
    }
}
