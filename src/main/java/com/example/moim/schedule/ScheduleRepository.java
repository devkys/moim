package com.example.moim.schedule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    @Query(value="select * from SCHEDULE where email =:email", nativeQuery = true)
    List<Schedule> getAll(String email);

    
}
