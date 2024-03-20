package com.example.moim.room;

import org.springframework.stereotype.Service;

@Service
public class RoomService {
    private final RoomRepository roomRepository;
    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }
    public Room insert(Room room) {
        return roomRepository.save(room);
    }

    public Room alreadyEnter(String email, Long sch_number) {
        return roomRepository.alreadyEnter(email, sch_number);
    }


}
