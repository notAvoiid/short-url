package com.abreu.short_url.services;

import com.abreu.short_url.exceptions.UrlExpiredException;
import com.abreu.short_url.exceptions.UrlNotFoundException;
import com.abreu.short_url.models.UrlEntity;
import com.abreu.short_url.repository.UrlRepository;
import com.abreu.short_url.utils.ShortCodeGenerator;
import com.abreu.short_url.utils.UrlValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.abreu.short_url.utils.Constants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlValidator urlValidator;

    @Mock
    private ShortCodeGenerator shortCodeGenerator;

    @InjectMocks
    private UrlService urlService;

    @BeforeEach
    void setUp() {
        urlService = new UrlService(urlRepository, urlValidator, shortCodeGenerator);
    }

    @Nested
    class ShortenUrl {

        @Test
        @DisplayName("Should return a valid UrlEntity when shortening a valid URL")
        void shortenUrl_ValidUrl_ShouldReturnUrlEntity() {
            UrlEntity savedEntity = new UrlEntity();
            savedEntity.setOriginalUrl(ORIGINAL_URL);
            savedEntity.setShortCode(SHORT_CODE);
            savedEntity.setExpiresAt(LocalDateTime.now().plusMinutes(MINUTES));

            when(urlRepository.save(any())).thenReturn(savedEntity);

            UrlEntity result = urlService.shortenUrl(ORIGINAL_URL, MINUTES);

            assertNotNull(result);
            assertEquals(ORIGINAL_URL, result.getOriginalUrl());
            assertNotNull(result.getShortCode());
            assertNotNull(result.getExpiresAt());

            verify(urlRepository, times(1)).save(any());
            verify(urlRepository, never()).existsByShortCode(any());

        }

        @Test
        @DisplayName("Should set default expiration time when not provided")
        void shortenUrl_NoExpirationProvided_ShouldUseDefaultExpiration() {
            UrlEntity savedEntity = new UrlEntity();
            savedEntity.setOriginalUrl(ORIGINAL_URL);
            savedEntity.setShortCode(SHORT_CODE);
            savedEntity.setExpiresAt(LocalDateTime.now().plusMinutes(MINUTES));

            when(urlRepository.save(any())).thenReturn(savedEntity);

            UrlEntity result = urlService.shortenUrl(ORIGINAL_URL, 0);

            assertNotNull(result.getExpiresAt());
            assertTrue(result.getExpiresAt().isAfter(LocalDateTime.now()));

            verify(urlRepository, times(1)).save(any());
            verify(urlRepository, never()).existsByShortCode(any());
        }
    }

    @Nested
    class GetOriginalUrl {

        @Test
        @DisplayName("Should throw UrlNotFoundException when the short code is invalid")
        void getOriginalUrl_InvalidShortCode_ShouldThrowException() {
            when(urlRepository.findByShortCode(INVALID)).thenReturn(Optional.empty());

            assertThrows(UrlNotFoundException.class, () ->
                    urlService.getOriginalUrl(INVALID)
            );
        }
    }
}


