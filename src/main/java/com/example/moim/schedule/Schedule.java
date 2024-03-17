package com.example.moim.schedule;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity(name="SCHEDULE")
@Getter
@Setter
@Data
public class Schedule {

    @Id
    @GeneratedValue
    private Long seq;
    private String title;
    private String content;
    private Date duedate;
    private String place;
}
