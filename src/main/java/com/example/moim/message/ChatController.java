package com.example.moim.message;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("api/chat")
@RequiredArgsConstructor
public class ChatController {


    private final ChatService chatService;

    @GetMapping("getAll")
    @ResponseBody
    public List<Chat> getMessage(@RequestParam("roomId") String id) {
        Long room_id=Long.parseLong(id);
        System.out.println("-------------" + room_id);
        return chatService.getAllBy(room_id);

    }
}
