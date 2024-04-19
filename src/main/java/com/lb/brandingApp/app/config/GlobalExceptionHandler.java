package com.lb.brandingApp.app.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import static com.lb.brandingApp.app.constants.ApplicationConstants.NOT_FOUND;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleException(RuntimeException e) {
        if(e.getMessage().contains(NOT_FOUND)) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
