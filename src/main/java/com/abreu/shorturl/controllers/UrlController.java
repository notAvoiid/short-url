package com.abreu.shorturl.controllers;

import com.abreu.shorturl.models.dto.UrlRequestDTO;
import com.abreu.shorturl.models.dto.UrlResponseDTO;
import com.abreu.shorturl.services.UrlService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UrlController {

    private final UrlService urlService;


    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/shorturl")
    public ResponseEntity<UrlResponseDTO> shortUrl(@RequestBody UrlRequestDTO request, HttpServletRequest servletRequest) {
        return ResponseEntity.ok(urlService.shortUrl(request, servletRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Void> redirect(@PathVariable String id) {
        HttpHeaders httpHeaders = urlService.redirect(id);
         if (httpHeaders.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.status(HttpStatus.FOUND).headers(httpHeaders).build();
    }
}
