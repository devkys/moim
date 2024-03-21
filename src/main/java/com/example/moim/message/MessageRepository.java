package com.example.moim.message;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    // 메시지 전송 (삽입)
    @Override
    <S extends Message> S save(S entity);

    @Query(value="select * from message where room_id=:room_id", nativeQuery = true)
    List<Message> getAllBy(Long room_id);
}
