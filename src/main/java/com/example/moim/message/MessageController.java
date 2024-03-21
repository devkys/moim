package com.example.moim.message;


import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MessageController {

    @MessageMapping("/recieve")

    @SendTo("/send")

    public void SocketHandler() {
        System.out.println("Handler");
    }


}
