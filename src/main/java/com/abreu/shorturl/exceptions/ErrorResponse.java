package com.abreu.shorturl.exceptions;

import lombok.Getter;

import java.time.Instant;
import java.util.Map;

@Getter
public class ErrorResponse {

    private final String code;
    private final String message;
    private final Instant timestamp;
    private final Map<String, String> fieldErrors;

    public ErrorResponse(String code, String message, Instant timestamp, Map<String, String> fieldErrors) {
        this.code = code;
        this.message = message;
        this.timestamp = timestamp;
        this.fieldErrors = fieldErrors;
    }
}