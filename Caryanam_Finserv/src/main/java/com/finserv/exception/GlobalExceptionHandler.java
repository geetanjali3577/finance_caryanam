package com.finserv.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(BadRequestException ex) {

        Map<String, Object> error = new HashMap<>();
        error.put("status", 400);
        error.put("message", ex.getMessage());

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {

        Map<String, Object> error = new HashMap<>();
        error.put("status", 500);
        error.put("message", ex.getMessage());

        return ResponseEntity.internalServerError().body(error);
    }
}