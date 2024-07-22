package com.wally.service;

import com.wally.model.Chat;
import com.wally.model.Message;
import com.wally.model.User;
import com.wally.repository.ChatRepository;
import com.wally.repository.MessageRepository;
import com.wally.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl  implements MessageService{

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ProjectService projectService;
    private final ChatRepository chatRepository;

    @Override
    public Message sendMessage(Long senderId, Long chatId, String content) throws Exception {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new Exception("User not found. ID: " + senderId));

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new Exception("Chat not found. ID: " + chatId));

        Message message = new Message();
        message.setContent(content);
        message.setSender(sender);
        message.setChat(chat);
        message.setCreatedAt(LocalDateTime.now());

        Message savedMessage = messageRepository.save(message);

        chat.getMessages().add(savedMessage);

        return savedMessage;
    }

    @Override
    public List<Message> getMessageByProjectId(Long projectId) throws Exception {
        Chat chat = projectService.getChatByProjectId(projectId);

        List<Message> messages = messageRepository.findByChatIdOrderByCreatedAtAsc(chat.getId());

        return messages;
    }
}
