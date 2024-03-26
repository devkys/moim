package com.example.moim.room;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("api/room_mgmt")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RoomController {

    private final RoomService roomService;

    @PostMapping("agree-invite")
    @ResponseBody
    public void inviteAgree(@RequestBody String data, HttpServletRequest req) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Room room = new Room();

        JsonNode rootNode = objectMapper.readTree(data);
        String email = rootNode.get("email").asText();

        HttpSession session = req.getSession();
        String sch_id = (String) session.getAttribute("sch_id");
        System.out.println("email: " + email);
        System.out.println("초대받은 스케줄 아이디: " + sch_id);
        System.out.println("request body String data: " + data);
        System.out.println("true or false : " + rootNode.get("choose").asText());

        if(rootNode.get("choose").asText().equals("true")) {
            room.setSch_number(Long.parseLong(sch_id.toString()));
            room.setEmail(email);
            roomService.insert(room);
        }

        // 초대 세션 종료
        session.invalidate();
   }


}
