package com.githubsalt.omoib.global.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponse> handleCommonException(Exception e) {
        ErrorResponse response = new ErrorResponse(e.getMessage());
        log.error("initial_timestamp: " + response.getTimestamp() + ", message: ", e);
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(response);
    }

}
