package com.example.moim.schedule;

import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public ScheduleService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    public List<Schedule> getAll(String email) {
        return scheduleRepository.getAll(email);
    }


}
