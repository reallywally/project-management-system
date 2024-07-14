package com.wally.service;

import com.wally.model.Chat;
import com.wally.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;

    public Chat createChat(Chat chat) throws Exception {
        return null;
    }
}
