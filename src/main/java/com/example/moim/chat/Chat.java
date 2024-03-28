package com.example.moim.chat;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.sql.Blob;
import java.sql.Timestamp;


@Getter
@Setter
@Entity(name="chat")
@Component
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long message_id;
    private String nickname;
    private Long room_id;
    private String content;

    @Column(nullable = false, columnDefinition = "timestamp default current_timestamp")
    private Timestamp send_time;
    private String blob_type;

}