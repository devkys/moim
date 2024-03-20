package com.example.moim.room;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room,Long> {

    @Override
    <S extends Room> S save(S entity);

    @Query(value = "select * from room where email =:email and sch_number=:sch_number ", nativeQuery = true)
    public Room alreadyEnter(String email, Long sch_number);

}
