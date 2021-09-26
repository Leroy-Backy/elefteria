package org.elefteria.elefteriasn.service;

import org.elefteria.elefteriasn.dto.ChatMessageDto;
import org.elefteria.elefteriasn.response.SuccessResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ChatMessageService {

    ResponseEntity<SuccessResponse> saveChatMessage(ChatMessageDto chatMessageDto);

    Page<ChatMessageDto> getAllMessages(Pageable pageable);

    List<ChatMessageDto> getMessagesAfter(Long afterId);
}
