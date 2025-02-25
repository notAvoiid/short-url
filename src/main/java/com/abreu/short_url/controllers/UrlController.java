package com.abreu.short_url.controllers;

import com.abreu.short_url.models.UrlEntity;
import com.abreu.short_url.models.dto.UrlRequestDTO;
import com.abreu.short_url.models.dto.UrlResponseDTO;
import com.abreu.short_url.services.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class UrlController {

    private final UrlService urlService;

    @GetMapping
    public ResponseEntity<List<UrlResponseDTO>> findAll() {
        return ResponseEntity.ok(urlService.findAll());
    }

    @PostMapping("/shorten")
    public ResponseEntity<UrlResponseDTO> shortenUrl(@Valid @RequestBody UrlRequestDTO request) {

        UrlEntity urlEntity = urlService.shortenUrl(request.url(), request.minutes());

        String shortUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/{shortCode}")
                .buildAndExpand(urlEntity.getShortCode())
                .toUriString();

        return ResponseEntity.status(HttpStatus.CREATED).body(new UrlResponseDTO(shortUrl, urlEntity.getExpiresAt(), urlEntity.getMinutes()));
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String shortCode) {
        String originalUrl = urlService.getOriginalUrl(shortCode);

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }

    @DeleteMapping("/cleanup")
    public ResponseEntity<Void> removeExpiredUrls() {
        urlService.removeExpiredUrls();
        return ResponseEntity.noContent().build();
    }
}