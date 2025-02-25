package com.abreu.short_url.models.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record UrlRequestDTO(

        @NotBlank(message = "The original URL is required\"")
        @Pattern(
                regexp = "^(?:(?:https?|ftp)://)?(?:www\\.)?([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}(?::[0-9]+)?(?:/.*)?$",
                message = "Invalid URL. Use the format: http(s)://www.example.com"
        )
        String url,

        @Positive(message = "The expiration time must be positive")
        @Min(value = 1, message = "The expiration time must be at least 1 minutes")
        int minutes
) {
    
}
