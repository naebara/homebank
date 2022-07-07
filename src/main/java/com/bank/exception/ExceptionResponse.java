package com.bank.exception;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
public class ExceptionResponse {

    private List<String> errors;
    private LocalDateTime dateTime;


}