package com.example.moim.room;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("api/room-mgmt")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RoomController {

    @PostMapping("agree-invite")
    @ResponseBody
    public String inviteAgree(@RequestBody String data) {

        System.out.println("응답받은 데이터");
        return "결과 받n음";

    }

}
