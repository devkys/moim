package com.example.moim.message;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {

    private final ChatRepository chatRepository;

    public ChatService(ChatRepository messageRepository) {
        this.chatRepository = messageRepository;
    }

    public List<Chat> getAllBy(Long room_id) {
        return chatRepository.getAllBy(room_id);
    }

}
