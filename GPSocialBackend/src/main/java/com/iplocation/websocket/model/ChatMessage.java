package com.iplocation.websocket.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {

    private MessageType type;
    private String content;
    private String sender;
    private String reciver;

    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE
    }
}
