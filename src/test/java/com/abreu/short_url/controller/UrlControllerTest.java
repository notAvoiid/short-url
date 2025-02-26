package com.abreu.short_url.controller;

import com.abreu.short_url.controllers.UrlController;
import com.abreu.short_url.models.UrlEntity;
import com.abreu.short_url.repository.UrlRepository;
import com.abreu.short_url.services.UrlService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static com.abreu.short_url.utils.Constants.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UrlController.class)
public class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UrlService urlService;

    @MockitoBean
    private UrlRepository urlRepository;

    @Test
    @DisplayName("POST /api/shorten - Valid request with custom expiration returns Created")
    void shortenUrl_ValidRequestWithCustomExpiration_ReturnsCreated() throws Exception {
        UrlEntity mockEntity = new UrlEntity();
        mockEntity.setShortCode(SHORT_CODE);
        mockEntity.setOriginalUrl(ORIGINAL_URL);
        mockEntity.setCreatedAt(LocalDateTime.now());
        mockEntity.setExpiresAt(LocalDateTime.now().plusMinutes(MINUTES));

        when(urlService.shortenUrl(anyString(), anyInt())).thenReturn(mockEntity);

        mockMvc.perform(post("/api/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"https://example.com\",\"minutes\":5}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shortUrl").value("http://localhost/api/abc123"))
                .andExpect(jsonPath("$.minutes").value(5));

        verify(urlService, times(1)).shortenUrl("https://example.com", 5);
    }

    @Test
    @DisplayName("POST /api/shorten - Valid request with default expiration returns Created")
    void shortenUrl_ValidRequestWithDefaultExpiration_ReturnsCreated() throws Exception {
        UrlEntity mockEntity = new UrlEntity();
        mockEntity.setShortCode("def456");
        mockEntity.setOriginalUrl("https://example.org");
        mockEntity.setCreatedAt(LocalDateTime.now());
        mockEntity.setExpiresAt(LocalDateTime.now().plusMinutes(3));

        when(urlService.shortenUrl(anyString(), anyInt())).thenReturn(mockEntity);

        mockMvc.perform(post("/api/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"https://example.org\", \"minutes\":3}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.minutes").value(3));

        verify(urlService, times(1)).shortenUrl("https://example.org", 3);
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid-url", "w.example.com", "ww.example.com", "htp://invalid.com", "", " "})
    @DisplayName("POST /api/shorten - Invalid request returns Bad Request")
    void shortenUrl_InvalidRequest_ReturnsBadRequest(String invalidUrl) throws Exception {
        mockMvc.perform(post("/api/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"" + invalidUrl + "\"}"))
                .andExpect(status().isBadRequest());

        verify(urlRepository, never()).save(any(UrlEntity.class));
    }

}
