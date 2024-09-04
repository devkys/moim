package com.example.moim.config.error;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.ResponseEntity;

/*
    Custom Error 내용을 담을 Response Entity를 생성한다.
 */
@Data
@Builder
public class ErrorResponseEntity {
    private int status;
    private String name;
    private String code;
    private String message;

    public static ResponseEntity<ErrorResponseEntity> toResponseEntity(ErrorCode e){
        return ResponseEntity
                .status(e.getHttpStatus())
                .body(ErrorResponseEntity.builder()
                        .status(e.getHttpStatus().value())
                        .name(e.name())
                        .code(e.getCode())
                        .message(e.getMessage())
                        .build());
    }
}
