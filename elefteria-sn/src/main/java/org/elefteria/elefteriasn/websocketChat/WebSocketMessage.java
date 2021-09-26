package org.elefteria.elefteriasn.websocketChat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WebSocketMessage {
    private String senderUsername;
    private String content;
}
