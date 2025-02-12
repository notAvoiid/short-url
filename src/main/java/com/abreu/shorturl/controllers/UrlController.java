package com.abreu.shorturl.controllers;

import com.abreu.shorturl.exceptions.UrlExpiredException;
import com.abreu.shorturl.exceptions.UrlNotFoundException;
import com.abreu.shorturl.models.UrlEntity;
import com.abreu.shorturl.models.dto.UrlRequestDTO;
import com.abreu.shorturl.models.dto.UrlResponseDTO;
import com.abreu.shorturl.services.UrlService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;

@RestController
@RequestMapping("/api")
@Slf4j
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
            request.getMinutes() != null ? request.getMinutes() : 3
        );

        String shortUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/{shortCode}")
                .buildAndExpand(urlEntity.getShortCode())
                .toUriString();

        log.info("A URL {} foi criada!", shortUrl);
        log.info("Tempo de expiração em: {}", urlEntity.getMinutes() + " minuto(s)");

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
            log.warn("A URL {} expirou e foi removida.", shortCode);
            response.sendRedirect("http://localhost:4200/expired");
        } catch (UrlExpiredException ex) {
            log.warn("A URL {} expirou e será removida.", shortCode);
            urlService.deleteExpiredUrl(shortCode);
            response.sendRedirect("http://localhost:4200/expired");
        }
    }
}