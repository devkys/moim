package com.example.moim.message;

import com.example.moim.member.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public List<Message> getAllBy(Long room_id) {
        return messageRepository.getAllBy(room_id);
    }

}
