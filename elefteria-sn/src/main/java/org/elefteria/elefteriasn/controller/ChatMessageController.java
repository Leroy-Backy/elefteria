package org.elefteria.elefteriasn.controller;

import org.elefteria.elefteriasn.dto.ChatMessageDto;
import org.elefteria.elefteriasn.response.SuccessResponse;
import org.elefteria.elefteriasn.service.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chatmessages")
public class ChatMessageController {

    private ChatMessageService chatMessageService;

    @Autowired
    public ChatMessageController(ChatMessageService chatMessageService) {
        this.chatMessageService = chatMessageService;
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER', 'ROLE_CITIZEN')")
    @GetMapping()
    public Page<ChatMessageDto> getAllMessages(@RequestParam(required = false, name = "page") Integer page,
                                               @RequestParam(required = false, name = "size") Integer size){

        if(page == null)
            page = 0;

        if(size == null)
            size = 40;

        Pageable pageable = PageRequest.of(page, size);

        return chatMessageService.getAllMessages(pageable);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER', 'ROLE_CITIZEN')")
    @PostMapping()
    public ResponseEntity<SuccessResponse> saveMessage(@ModelAttribute ChatMessageDto messageDto){
        return chatMessageService.saveChatMessage(messageDto);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER', 'ROLE_CITIZEN')")
    @GetMapping("/after/{afterId}")
    public List<ChatMessageDto> getNewMessages(@PathVariable("afterId") Long afterId){
        return chatMessageService.getMessagesAfter(afterId);
    }
}
