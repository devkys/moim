package com.example.moim;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class MoimApplication {
    public static void main(String[] args) {
        SpringApplication.run(MoimApplication.class, args);
    }
}
