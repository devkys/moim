package com.example.moim.schedule;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

@Controller
@RequestMapping("api/sch_mgmt")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping("main-board")
    public void getAllPost() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Authorization", access_token);
        WebClient webClient = WebClient.create();
        String mainInfo = webClient.get()
                .uri("https://dev-api.cyber-i.com/svc/moim/main?email=" + )
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    @GetMapping("invite-sch/{id}")
    public RedirectView inviteSchedule(@PathVariable("id") int id) {
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl("http://localhost:5173/login");
        return redirectView;
    }

}
