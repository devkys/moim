package com.example.moim.config.security;


import lombok.AllArgsConstructor;
import lombok.Getter;


/*
    runtimeexcetion 상속 받아서 런타임 때 예외 발생
 */
@Getter
@AllArgsConstructor
public class CustomException extends RuntimeException{
    ErrorCode errorCode;
}
