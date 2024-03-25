package com.example.moim.chat;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;


@Controller
@RequiredArgsConstructor
public class StompController {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final SimpMessagingTemplate template; //특정 Broker로 메세지를 전달
    private final ChatService chatService;

    //Client가 SEND할 수 있는 경로
    //stompConfig에서 설정한 applicationDestinationPrefixes와 @MessageMapping 경로가 병합됨
    //"/pub/chat/enter"
    @MessageMapping(value = "/chat/join")
    public void enter(Chat chat){
        chat.setContent(chat.getEmail() + "님이 참여하였습니다");
        template.convertAndSend("/sub/chat/room" + chat.getRoom_id(), chat);
    }

    @MessageMapping(value = "/chat/message")
    public void message(Chat chat){
        template.convertAndSend("/sub/chat/room/"+ chat.getRoom_id(), chat);
        chatService.save(chat);
    }



}
