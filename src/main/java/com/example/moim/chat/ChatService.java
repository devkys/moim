package com.example.moim.chat;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
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

    public Chat save (Chat chat) {
        return chatRepository.save(chat);
    }

}
