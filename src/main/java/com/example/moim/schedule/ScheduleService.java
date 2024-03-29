package com.example.moim.schedule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public String getEmail(Long id) {
        return scheduleRepository.getEmail(id);
    }

    public List<Schedule> getInvitedSchedule(String email) {
        return scheduleRepository.getInvitedSchedule(email);
    }

    public Schedule save(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    public void deletebyId(Long s_id) {
        scheduleRepository.deleteById(s_id);
    }
    public void update(Schedule schedule) {
        scheduleRepository.update(schedule.getTitle(), schedule.getContent(), schedule.getDuedate(), schedule.getPlace(), schedule.getSeq());
    }

}
