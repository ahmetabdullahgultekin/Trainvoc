package com.rollingcatsoftware.trainvocmultiplayerapplication.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RoomPasswordException.class)
    public ResponseEntity<Map<String, String>> handleRoomPasswordException(RoomPasswordException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", ex.getError());
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }
}

