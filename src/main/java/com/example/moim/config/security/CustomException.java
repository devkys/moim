package com.example.moim.config.security;


import lombok.AllArgsConstructor;
import lombok.Getter;


/*
    error code를 담을 class 생성
 */
@Getter
@AllArgsConstructor
public class CustomException extends RuntimeException{
    ErrorCode errorCode;
}
