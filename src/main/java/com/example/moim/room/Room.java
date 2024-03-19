package com.example.moim.room;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Entity(name="ROOM")
@Getter
@Setter
@Data
@Component
public class Room {

    @Id
    @GeneratedValue
    private Long seq;
    private String email;
    private String sch_number;
    private String role;

}
