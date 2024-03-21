package com.example.moim.message;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class StompController {

    private final SimpMessagingTemplate template; //특정 Broker로 메세지를 전달

    private final MessageRepository messageRepository;

    //Client가 SEND할 수 있는 경로
    //stompConfig에서 설정한 applicationDestinationPrefixes와 @MessageMapping 경로가 병합됨
    //"/pub/chat/enter"
    @MessageMapping(value = "/chat/join")
    public void enter(Message message){
        System.out.println("message mapping 실행됨");
//        message.setMessage(message.getWriter() + "님이 채팅방에 참여하였습니다.");
//        template.convertAndSend("/subscribe/chat/room/" + message.getRoomId(), message);
    }

    @MessageMapping(value = "/chat/message")
    public void message(Message message){
        template.convertAndSend("/subscribe/chat/room/" + message.getRoom_id(), message);
    }


}
