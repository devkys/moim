package com.example.moim.room;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;
    public Room insert(Room room) {
        return roomRepository.save(room);
    }
    public Room alreadyEnter(String email, Long sch_number) {
        return roomRepository.alreadyEnter(email, sch_number);
    }

}
