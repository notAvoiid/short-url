package com.abreu.short_url.controllers;

import com.abreu.short_url.models.UrlEntity;
import com.abreu.short_url.models.dto.UrlRequestDTO;
import com.abreu.short_url.models.dto.UrlResponseDTO;
import com.abreu.short_url.services.UrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "URL Controller", description = "Endpoints for URL shortening and redirection")
public class UrlController {

    private final UrlService urlService;

    @GetMapping("/")
    public String home() {
        return "Hello, World!";
    }

    @GetMapping
    @Operation(
            summary = "Retrieve all shortened URLs",
            description = "Fetches a list of all shortened URLs stored in the database"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<UrlResponseDTO>> findAll() {
        return ResponseEntity.ok(urlService.findAll());
    }

    @PostMapping("/shorten")
    @Operation(
            summary = "Shorten URL",
            description = "Create a shortened version of a URL with configurable expiration time"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Short URL created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<UrlResponseDTO> shortenUrl(@Valid @RequestBody UrlRequestDTO request) {

        UrlEntity urlEntity = urlService.shortenUrl(request.url(), request.minutes());

        String shortUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/{shortCode}")
                .buildAndExpand(urlEntity.getShortCode())
                .toUriString();

        return ResponseEntity.status(HttpStatus.CREATED).body(new UrlResponseDTO(shortUrl, urlEntity.getExpiresAt(), urlEntity.getMinutes()));
    }

    @GetMapping("/{shortCode}")
    @Operation(
            summary = "Redirect to the original URL",
            description = "Redirects the user to the original URL associated with the given short code"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "302", description = "Successful redirection"),
            @ApiResponse(responseCode = "404", description = "Short code does not exist"),
            @ApiResponse(responseCode = "410", description = "URL expired"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String shortCode) {
        String originalUrl = urlService.getOriginalUrl(shortCode);

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }

    @DeleteMapping("/cleanup")
    @Operation(
            summary = "Remove expired URLs",
            description = "Deletes all expired shortened URLs from the database"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Expired URLs removed successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> removeExpiredUrls() {
        urlService.removeExpiredUrls();
        return ResponseEntity.noContent().build();
    }

}