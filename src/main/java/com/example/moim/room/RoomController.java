package com.example.moim.room;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("api/room_mgmt")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping("agree-invite")
    @ResponseBody
    public String inviteAgree(@RequestBody String data, HttpServletRequest req) throws JsonProcessingException {
        System.out.println("DATAAAAAAAAAAAAAA" + data);
        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println("data___" + data);
        JsonNode rootNode = objectMapper.readTree(data);
        System.out.println(rootNode + "rootnode");
//        String choose = rootNode.get("choose").asText();
        String email = rootNode.get("email").asText();

        if(rootNode.get("choose").asText().equals("true")) {
            Room room = new Room();
            HttpSession session = req.getSession();
            String sch_id = (String) session.getAttribute("sch_id");
            room.setSch_number(sch_id);
            room.setEmail(email);
            Room created_room = roomService.insert(room);
            System.out.println(created_room);
            System.out.println("응답받은 데이터");
            System.out.println(data);
        }

        return "결과 받n음";

    }

}
