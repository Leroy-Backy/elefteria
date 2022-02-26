package org.elefteria.elefteriasn.dto;

import lombok.Getter;
import lombok.Setter;
import org.elefteria.elefteriasn.entity.ChatMessage;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Base64;

@Getter
@Setter
public class ChatMessageDto {
    public Long id;
    private String message;
    private String image;
    private String user;
    private LocalDateTime createdDate;

    private MultipartFile file;

    public ChatMessageDto(){}

    public ChatMessageDto(ChatMessage chatMessage){
        this.message = chatMessage.getMessage();
        this.user = chatMessage.getUser().getUsername();
        this.createdDate = chatMessage.getCreatedDate();
        this.id = chatMessage.getId();

        if(chatMessage.getImage() != null)
            this.image = chatMessage.getImage().getFileName();
    }

    public static ChatMessageDto formatToChatMessageDto(ChatMessage chatMessage){
        return new ChatMessageDto(chatMessage);
    }
}
