package com.lb.brandingApp.app.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Objects;

import static com.lb.brandingApp.app.constants.ApplicationConstants.NOT_FOUND;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleException(RuntimeException e) {
        log.error("GlobalExceptionHandler caught Exception: ", e);
        if(Objects.nonNull(e.getMessage()) && e.getMessage().contains(NOT_FOUND)) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
