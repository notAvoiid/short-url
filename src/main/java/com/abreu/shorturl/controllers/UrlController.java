package com.abreu.shorturl.controllers;

import com.abreu.shorturl.exceptions.UrlExpiredException;
import com.abreu.shorturl.exceptions.UrlNotFoundException;
import com.abreu.shorturl.models.UrlEntity;
import com.abreu.shorturl.models.dto.UrlRequestDTO;
import com.abreu.shorturl.models.dto.UrlResponseDTO;
import com.abreu.shorturl.services.UrlService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class UrlController {

    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/shorten")
    @ResponseStatus(HttpStatus.CREATED)
    public UrlResponseDTO shortenUrl(@Valid @RequestBody UrlRequestDTO request) {
        UrlEntity urlEntity = urlService.shortenUrl(
            request.getUrl(),
            request.getMinutes() != null ? request.getMinutes() : 3 // Default 3 minutos
        );

        String shortUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/{shortCode}")
                .buildAndExpand(urlEntity.getShortCode())
                .toUriString();

        return new UrlResponseDTO(shortUrl, urlEntity.getExpiresAt(), urlEntity.getMinutes());
    }

    @GetMapping("/{shortCode}")
    public void redirectToOriginalUrl(
            @PathVariable String shortCode,
            HttpServletResponse response
    ) throws IOException {
        try {
            String originalUrl = urlService.getOriginalUrl(shortCode);
            response.sendRedirect(originalUrl);

        } catch (UrlNotFoundException ex) {
            response.sendError(HttpStatus.NOT_FOUND.value(), ex.getMessage());

        } catch (UrlExpiredException ex) {
            urlService.deleteExpiredUrl(shortCode); // Remove do cache e banco
            response.sendError(HttpStatus.GONE.value(), ex.getMessage());
        }
    }
}