package com.example.moim.schedule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    @Query(value="select * from SCHEDULE where email =:email", nativeQuery = true)
    List<Schedule> getAll(String email);

    @Override
    List<Schedule> findAllById(Iterable<Long> longs);

    @Query(value="select email from SCHEDULE where seq =:seq", nativeQuery = true)
    String getEmail(Long seq);

    @Query(value="select * from schedule where seq in (select sch_number from room where email=:email)", nativeQuery = true)
    List<Schedule> getInvitedSchedule(String email);
}
