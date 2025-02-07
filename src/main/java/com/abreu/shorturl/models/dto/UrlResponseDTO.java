package com.abreu.shorturl.models.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UrlResponseDTO {

    private final String shortUrl;
    private final LocalDateTime expiresAt;
    private final int minutes;

    public UrlResponseDTO(String shortUrl, LocalDateTime expiresAt, int minutes) {
        this.shortUrl = shortUrl;
        this.expiresAt = expiresAt;
        this.minutes = minutes;
    }
}
