package com.example.moim.room;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room,Long> {
    // 일정에 유저 초대
    @Override
    <S extends Room> S save(S entity);

    // 이미 초대된 방인지 확인
    @Query(value = "select * from room where email =:email and sch_number=:sch_number ", nativeQuery = true)
    Room alreadyEnter(String email, Long sch_number);

}
