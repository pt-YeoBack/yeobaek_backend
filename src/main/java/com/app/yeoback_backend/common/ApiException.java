package com.app.yeoback_backend.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/** 서비스에서 던지면 GlobalExceptionHandler 가 {"message": ...} 와 상태코드로 바꿔 응답한다. */
@Getter
public class ApiException extends RuntimeException {

    private final HttpStatus status;

    public ApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
}
