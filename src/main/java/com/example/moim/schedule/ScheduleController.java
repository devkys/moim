package com.example.moim.schedule;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

import static com.example.moim.member.MemberController.access_token;

@Controller
@RequestMapping("api/sch_mgmt")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ScheduleController {
    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping("main-board")
    @ResponseBody
    public List<Schedule> getAllPost(@RequestParam("email") String email) {
        System.out.println("schedule controller access token :" + access_token);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Authorization", "b7a3565e8354402b9d4d2cdcc51bd302");
        WebClient webClient = WebClient.create();
        String mainInfo = webClient.get()
                .uri("https://dev-api.cyber-i.com/svc/moim/main?email=" + email )
                .headers(h-> h.addAll(headers))
                .retrieve()
                .bodyToMono(String.class)
                .block();
        System.out.println("main board controller email: "  + email);
        return scheduleService.getAll(email);
    }

    @GetMapping("invite-sch/{id}")
    public RedirectView inviteSchedule(@PathVariable("id") String id, HttpServletRequest req) {
        HttpSession session = req.getSession();
        session.setAttribute("sch_id", id);
        System.out.println("--------------------------" + session.getAttribute("sch_id"));
        return new RedirectView("http://localhost:5173/login");
    }

    @GetMapping("check-invite")
    @ResponseBody
    public String confirmInvite(HttpServletRequest req) {
        HttpSession session = req.getSession();
        String sch_id = (String)session.getAttribute("sch_id");
        if(sch_id.isEmpty()) {
            System.out.println("초대장 없음");
        } else {
            System.out.println("초대장 있음 " + sch_id);
        }
        return sch_id;
    }


}
