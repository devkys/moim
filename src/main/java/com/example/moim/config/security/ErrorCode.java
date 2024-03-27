package com.example.moim.config.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/*
    사용할 error code 정의
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    USER_NOT_FOUND(HttpStatus.UNAUTHORIZED, "ACCOUNT-001", "사용자를 찾을 수 없습니다."),
    HAS_EMAIL(HttpStatus.BAD_REQUEST, "ACCOUNT-002", "존재하는 이메일입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "ACCOUNT-003", "비밀번호가 일치하지 않습니다."),
    UNAUTHORIZED_KEY(HttpStatus.UNAUTHORIZED, "ACCOUNT-004", "권한이 없는 사용자입니다."),
    WRONG_ACCESS(HttpStatus.BAD_REQUEST, "ACCOUNT-005", "잘못된 접근입니다."),
    METHOD_ERROR(HttpStatus.METHOD_NOT_ALLOWED, "ACCOUNT006", "지정되지 않은 메소드 타입입니다");

    private final HttpStatus httpStatus;	// HttpStatus
    private final String code;				// ACCOUNT-001
    private final String message;

    }
