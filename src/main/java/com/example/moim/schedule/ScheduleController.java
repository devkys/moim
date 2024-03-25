package com.example.moim.schedule;

import com.example.moim.room.Room;
import com.example.moim.room.RoomService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.Optional;

import static com.example.moim.member.MemberController.access_token;

@Controller
@RequestMapping("api/sch_mgmt")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ScheduleController {
    private final ScheduleService scheduleService;

    private final RoomService roomService;

    public ScheduleController(ScheduleService scheduleService, RoomService roomService) {
        this.scheduleService = scheduleService;
        this.roomService = roomService;
    }

    /*
        로그인한 유저의 스케쥴 리스트로 반환
     */
    @GetMapping("main-board")
    @ResponseBody
    public List<Schedule> getAllPost(@RequestParam("email") String email) {
        System.out.println("schedule controller access token :" + access_token);
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Content-Type", "application/json");
//        headers.add("Authorization", "b7a3565e8354402b9d4d2cdcc51bd302");
//        WebClient webClient = WebClient.create();
//        String mainInfo = webClient.get()
//                .uri("https://dev-api.cyber-i.com/svc/moim/main?email=" + email )
//                .headers(h-> h.addAll(headers))
//                .retrieve()
//                .bodyToMono(String.class)
//                .block();
        System.out.println("main board controller email: "  + email);
        return scheduleService.getAll(email);
    }

    @GetMapping("invite-board")
    @ResponseBody
    public List<Schedule> invitedGet(@RequestParam("email") String email) {
        return scheduleService.getInvitedSchedule(email);
    }


    /*
        초대 링크 클릭시, 세션 생성과 로그인 화면으로 리다이렉션
     */
    @GetMapping("invite-sch/{id}")
    public RedirectView inviteSchedule(@PathVariable("id") String id, HttpServletRequest req) {
        HttpSession session = req.getSession();
        session.setAttribute("sch_id", id);
        System.out.println("--------------------------" + session.getAttribute("sch_id"));
        return new RedirectView("http://localhost:5173/login");
    }

    /*
        초대된 일정 세션 존재하는지 확인
        존재 시 초대된 일정 pk값 리턴
     */
    @GetMapping("check-invite")
    @ResponseBody
    public String confirmInvite(HttpServletRequest req, @RequestParam("email") String email) throws IllegalAccessException {

        HttpSession session = req.getSession();
        Object sch_id = session.getAttribute("sch_id");

        // 세션 값 null
        if(sch_id == null) {
            System.out.println("초대장 없음");
            return "false";
        } else {
            /*
                세션 값 not null
             */
            Room room = roomService.alreadyEnter(email, (Long.parseLong(sch_id.toString())));
            if(scheduleService.getEmail(Long.parseLong(sch_id.toString())).equals(email) || room != null) {
                // 세션 값은 있지만 이미 초대된 방의 링크를 클릭했을 때 세션 끊기
                session.invalidate();
                return "false";
            }
            System.out.println("초대장 있음 " + sch_id);
            return "true";
        }
    }

    @PostMapping("save")
    @ResponseBody
    public boolean SaveSchedule(@RequestBody Schedule reqData){
        System.out.println("생성하고 싶은 일정 " + reqData);
        scheduleService.save(reqData);
        return true;
    }

    @PostMapping("deleteOne")
    @ResponseBody
    public int deleteSchedule(@RequestBody String scheduleId) {

        System.out.println("스케쥴 삭제" + scheduleId);
        scheduleService.deletebyId(Long.parseLong(scheduleId));

        return 1;
    }

}
