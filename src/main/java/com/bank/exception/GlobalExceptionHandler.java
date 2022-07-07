package com.bank.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ExceptionResponse> handleRequestBodyError(WebExchangeBindException ex) {
        log.error("Exception caught : {}", ex.getMessage(), ex);


        List<String> errors = ex.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .sorted()
                .collect(Collectors.toList());
        ExceptionResponse response = ExceptionResponse.builder()
                .errors(errors)
                .dateTime(LocalDateTime.now())
                .build();

        log.error("Errors are : {} ", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ExceptionResponse> handleError(Throwable ex) {
        log.error("Error : {}", ex.getMessage());
        ExceptionResponse responseError = ExceptionResponse
                .builder()
                .errors(Collections.singletonList(ex.getMessage()))
                .dateTime(LocalDateTime.now()).build();
        return new ResponseEntity<>(responseError, HttpStatus.BAD_REQUEST);
    }

}
