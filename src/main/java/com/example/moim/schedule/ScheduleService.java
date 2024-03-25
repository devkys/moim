package com.example.moim.schedule;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public ScheduleService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    // 로그인 유저의 전체 스케쥴 리스트로 가져오기
    public List<Schedule> getAll(String email) {
        return scheduleRepository.getAll(email);
    }

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
