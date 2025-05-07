package com.pharmacy.inventory.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<Object> handleHttpMediaTypeNotAcceptableException(
            HttpMediaTypeNotAcceptableException ex, WebRequest request) {
        
        logger.error("Media type not acceptable: {}", ex.getMessage());
        logger.error("Supported media types: {}", ex.getSupportedMediaTypes());
        logger.error("Request headers: {}", request.getHeaderValues("Accept"));
        
        Map<String, Object> body = new HashMap<>();
        body.put("message", "Supported media types are: " + ex.getSupportedMediaTypes());
        body.put("error", "Not Acceptable");
        body.put("status", HttpStatus.NOT_ACCEPTABLE.value());
        
        return new ResponseEntity<>(body, new HttpHeaders(), HttpStatus.NOT_ACCEPTABLE);
    }
}
