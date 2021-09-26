package org.elefteria.elefteriasn.service;

import org.elefteria.elefteriasn.dao.ChatMessageRepository;
import org.elefteria.elefteriasn.dao.ImageRepository;
import org.elefteria.elefteriasn.dao.UserRepository;
import org.elefteria.elefteriasn.dto.ChatMessageDto;
import org.elefteria.elefteriasn.entity.ChatMessage;
import org.elefteria.elefteriasn.entity.Image;
import org.elefteria.elefteriasn.entity.User;
import org.elefteria.elefteriasn.response.SuccessResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChatMessageServiceImpl implements ChatMessageService{

    private ChatMessageRepository chatMessageRepository;
    private UserRepository userRepository;
    private FileService fileService;

    @Autowired
    public ChatMessageServiceImpl(ChatMessageRepository chatMessageRepository, UserRepository userRepository, FileService fileService) {
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
        this.fileService = fileService;
    }

    @Override
    @Transactional
    public ResponseEntity<SuccessResponse> saveChatMessage(ChatMessageDto chatMessageDto) {
        Optional<User> userOptional = userRepository.findByUsername(chatMessageDto.getUser());
        if(userOptional.isEmpty()){
            throw new UsernameNotFoundException("User not found with username: " + chatMessageDto.getUser());
        }

        ChatMessage chatMessage = new ChatMessage(chatMessageDto.getMessage(), userOptional.get());

        MultipartFile file = chatMessageDto.getFile();

        if(file != null){
            String fileName = fileService.uploadImageToLocalFileSystem(file);

            Image image = new Image(fileName);

            chatMessage.setImage(image);
        }

        chatMessageRepository.save(chatMessage);
        SuccessResponse response = new SuccessResponse(
                HttpStatus.OK.value(),
                "message send",
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    @Transactional
    public Page<ChatMessageDto> getAllMessages(Pageable pageable) {
        Page<ChatMessage> chatMessages = chatMessageRepository.getAllOrderByCreatedDateDesc(pageable);

        Page<ChatMessageDto> chatMessageDtos = chatMessages.map(ChatMessageDto::formatToChatMessageDto);

        return chatMessageDtos;
    }

    @Override
    @Transactional
    public List<ChatMessageDto> getMessagesAfter(Long afterId) {
        return chatMessageRepository.getMessagesAfter(afterId).stream().map(ChatMessageDto::new).collect(Collectors.toList());
    }
}
