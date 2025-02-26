package com.abreu.short_url.exceptions;

import com.abreu.short_url.exceptions.handler.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import static com.abreu.short_url.utils.Constants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpStatus.*;

@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @Mock
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
    }

    @Nested
    @DisplayName("General Exception Tests")
    class GeneralExceptionTests {

        @Test
        @DisplayName("Handle all exceptions with full error response")
        void handleAllExceptionsReturnsFullErrorResponse() {
            var response = exceptionHandler.handleAllExceptions(
                    new Exception(INTERNAL_ERROR),
                    request
            );

            assertNotNull(response);
            assertNotNull(response.getBody());
            assertNotNull(response.getBody().getTimestamp());
            assertNotNull(response.getBody().getMethod());
            assertNotNull(response.getBody().getPath());

            assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
            assertEquals(request.getMethod(), response.getBody().getMethod());
            assertEquals(request.getRequestURI(), response.getBody().getPath());

            assertEquals(ResponseEntity.class, response.getClass());
            assertEquals(ErrorMessage.class, response.getBody().getClass());
            assertEquals(INTERNAL_ERROR, response.getBody().getMessage());
            assertEquals(500, response.getBody().getStatus());
        }

        @Test
        @DisplayName("Handle all exceptions with minimal response")
        void handleAllExceptionsReturnsMinimalResponse() {
            var response = exceptionHandler.handleAllExceptions(
                    new IllegalArgumentException(INTERNAL_ERROR),
                    request
            );

            assertNotNull(response);
            assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
        }
    }

    @Nested
    @DisplayName("URL Not Found Exception Tests")
    class UrlNotFoundExceptionTests {

        @Test
        @DisplayName("Handle URL not found exception with full error response")
        void handleUrlNotFoundExceptionReturnsFullErrorResponse() {
            var response = exceptionHandler.handleUrlNotFoundException(
                    new UrlNotFoundException(URL_NOT_FOUND),
                    request
            );

            assertNotNull(response);
            assertNotNull(response.getBody());
            assertNotNull(response.getBody().getTimestamp());
            assertNotNull(response.getBody().getMethod());
            assertNotNull(response.getBody().getPath());

            assertEquals(NOT_FOUND, response.getStatusCode());
            assertEquals(request.getMethod(), response.getBody().getMethod());
            assertEquals(request.getRequestURI(), response.getBody().getPath());

            assertEquals(ResponseEntity.class, response.getClass());
            assertEquals(ErrorMessage.class, response.getBody().getClass());
            assertEquals(URL_NOT_FOUND, response.getBody().getMessage());
            assertEquals(404, response.getBody().getStatus());
        }

        @Test
        @DisplayName("Handle URL not found exception with null request")
        void handleUrlNotFoundExceptionWithNullRequest() {
            var response = exceptionHandler.handleUrlNotFoundException(
                    new UrlNotFoundException(URL_NOT_FOUND),
                    null
            );

            assertNotNull(response);
            assertEquals(NOT_FOUND, response.getStatusCode());
        }
    }

    @Nested
    @DisplayName("URL Expired Exception Tests")
    class UrlExpiredExceptionTests {

        @Test
        @DisplayName("Handle URL expired exception with full error response")
        void handleUrlExpiredExceptionReturnsFullErrorResponse() {
            var response = exceptionHandler.handleUrlExpiredException(
                    new UrlExpiredException(URL_EXPIRED),
                    request
            );

            assertNotNull(response);
            assertNotNull(response.getBody());
            assertNotNull(response.getBody().getTimestamp());
            assertNotNull(response.getBody().getMethod());
            assertNotNull(response.getBody().getPath());

            assertEquals(GONE, response.getStatusCode());
            assertEquals(request.getMethod(), response.getBody().getMethod());
            assertEquals(request.getRequestURI(), response.getBody().getPath());

            assertEquals(ResponseEntity.class, response.getClass());
            assertEquals(ErrorMessage.class, response.getBody().getClass());
            assertEquals(URL_EXPIRED, response.getBody().getMessage());
            assertEquals(410, response.getBody().getStatus());
        }

        @Test
        @DisplayName("Handle URL expired exception with null request")
        void handleUrlExpiredExceptionWithNullRequest() {
            var response = exceptionHandler.handleUrlExpiredException(
                    new UrlExpiredException(URL_EXPIRED),
                    null
            );

            assertNotNull(response);
            assertEquals(GONE, response.getStatusCode());
        }
    }

    @Nested
    @DisplayName("Illegal Argument Exception Tests")
    class IllegalArgumentExceptionTests {

        @Test
        @DisplayName("Handle illegal argument exception with full error response")
        void handleIllegalArgumentExceptionReturnsFullErrorResponse() {
            var response = exceptionHandler.handleIllegalArgumentException(
                    new IllegalArgumentException(INTERNAL_ERROR),
                    request
            );

            assertNotNull(response);
            assertNotNull(response.getBody());
            assertNotNull(response.getBody().getTimestamp());
            assertNotNull(response.getBody().getMethod());
            assertNotNull(response.getBody().getPath());

            assertEquals(BAD_REQUEST, response.getStatusCode());
            assertEquals(request.getMethod(), response.getBody().getMethod());
            assertEquals(request.getRequestURI(), response.getBody().getPath());

            assertEquals(ResponseEntity.class, response.getClass());
            assertEquals(ErrorMessage.class, response.getBody().getClass());
            assertEquals(INTERNAL_ERROR, response.getBody().getMessage());
            assertEquals(400, response.getBody().getStatus());
        }

        @Test
        @DisplayName("Handle illegal argument exception with minimal response")
        void handleIllegalArgumentExceptionReturnsMinimalResponse() {
            var response = exceptionHandler.handleIllegalArgumentException(
                    new IllegalArgumentException(INTERNAL_ERROR),
                    request
            );

            assertNotNull(response);
            assertEquals(BAD_REQUEST, response.getStatusCode());
        }
    }

}
