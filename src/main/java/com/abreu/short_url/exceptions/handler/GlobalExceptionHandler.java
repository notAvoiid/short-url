package com.abreu.short_url.exceptions.handler;

import com.abreu.short_url.exceptions.ErrorMessage;
import com.abreu.short_url.exceptions.UrlExpiredException;
import com.abreu.short_url.exceptions.UrlNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.net.URI;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String ERROR_PREFIX = "API Error";

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleAllExceptions(Exception ex, HttpServletRequest request) {
        log.error("{} - URI: {}", ERROR_PREFIX, request != null ? request.getRequestURI() : "N/A", ex);
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .contentType(APPLICATION_JSON)
                .body(buildErrorMessage(request, INTERNAL_SERVER_ERROR, "An internal error occurred!"));
    }

    @ExceptionHandler({UrlNotFoundException.class})
    public final ResponseEntity<ErrorMessage> handleUrlNotFoundException(UrlNotFoundException ex, HttpServletRequest request) {
        log.error("{} - URI: {}", ERROR_PREFIX, request != null ? request.getRequestURI() : "N/A", ex);
        return ResponseEntity
                .status(NOT_FOUND)
                .contentType(APPLICATION_JSON)
                .body(buildErrorMessage(request, NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler({UrlExpiredException.class})
    public final ResponseEntity<ErrorMessage> handleUrlExpiredException(UrlExpiredException ex, HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.error("{} - URI: {}", ERROR_PREFIX, request != null ? request.getRequestURI() : "N/A", ex);
        response.sendRedirect("http://localhost:4200/expired");
        return ResponseEntity
                .status(GONE)
                .location(URI.create("http://localhost:4200/expired"))
                .contentType(APPLICATION_JSON)
                .body(buildErrorMessage(request, GONE, ex.getMessage()));
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public final ResponseEntity<ErrorMessage> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        log.error("{} - URI: {}", ERROR_PREFIX, request != null ? request.getRequestURI() : "N/A", ex);
        return ResponseEntity
                .badRequest()
                .contentType(APPLICATION_JSON)
                .body(buildErrorMessage(request, BAD_REQUEST, ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public final ResponseEntity<ErrorMessage> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        if (request == null) {
            log.error("{} - URI: {}", ERROR_PREFIX, "N/A", ex);
            return ResponseEntity.badRequest().contentType(APPLICATION_JSON).body(new ErrorMessage(BAD_REQUEST, ex.getMessage()));
        }

        log.error("{} - URI: {}", ERROR_PREFIX, request.getRequestURI(), ex);
        return ResponseEntity
                .badRequest()
                .contentType(APPLICATION_JSON)
                .body(new ErrorMessage(request, BAD_REQUEST, "Validation error in fields", ex.getBindingResult()));

    }

    private ErrorMessage buildErrorMessage(HttpServletRequest request, HttpStatus status, String message) {
        return request != null ? new ErrorMessage(request, status, message) : new ErrorMessage(status, message);
    }
}