package com.example.moim.schedule;

import org.springframework.stereotype.Service;
import java.util.List;
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




}
