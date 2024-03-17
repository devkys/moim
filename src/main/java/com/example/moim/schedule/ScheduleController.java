package com.example.moim.schedule;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("api/sch_mgmt")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping("all")
    @ResponseBody
    public List<Schedule> getAllPost(@RequestParam(value = "user") String email) {
        return scheduleService.getAll(email);

    }

}
