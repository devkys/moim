package com.example.moim.schedule;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Data
@Component
public class ScheduleDTO {

    private String seq;
    private String title;
    private String content;
    private String duedate;
    private String place;

}
