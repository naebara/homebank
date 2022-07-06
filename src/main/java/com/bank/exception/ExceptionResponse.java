package com.bank.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
public class ExceptionResponse {

    private List<String> errors;
    private LocalDateTime dateTime;


}