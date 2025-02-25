package com.abreu.short_url.models.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record UrlResponseDTO(
        String shortUrl,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss", timezone = "America/Sao_Paulo")
        LocalDateTime expiresAt,
        int minutes
) { }
