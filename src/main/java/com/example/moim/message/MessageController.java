package com.example.moim.message;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
;

@Controller
public class MessageController {

    @GetMapping("/chat")
    public String chatGET(){
        System.out.println("@MessageController, chatGET()");

        return "chat";
    }

}
