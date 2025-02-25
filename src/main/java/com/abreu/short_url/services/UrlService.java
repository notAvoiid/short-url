package com.abreu.short_url.services;

import com.abreu.short_url.exceptions.UrlExpiredException;
import com.abreu.short_url.exceptions.UrlNotFoundException;
import com.abreu.short_url.models.UrlEntity;
import com.abreu.short_url.models.dto.UrlResponseDTO;
import com.abreu.short_url.repository.UrlRepository;
import com.abreu.short_url.utils.ShortCodeGenerator;
import com.abreu.short_url.utils.UrlValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;
    private final UrlValidator urlValidator;
    private final ShortCodeGenerator shortCodeGenerator;

    @Transactional(readOnly = true)
    public List<UrlResponseDTO> findAll() {
        log.info("Fetching all URLs");
        return urlRepository.findAll().stream()
                .map(urlEntity -> new UrlResponseDTO(urlEntity.getShortCode(), urlEntity.getExpiresAt(), urlEntity.getMinutes()))
                .toList();
    }

    @Transactional
    public UrlEntity shortenUrl(String originalUrl, int minutes) {
        String formattedUrl = urlValidator.validateFormat(originalUrl);
        String shortCode = shortCodeGenerator.generateUniqueShortCode();
        log.info("Shortening URL: {} to {}", formattedUrl, shortCode);

        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime expiresAt = createdAt.plusMinutes(minutes);

        UrlEntity urlEntity = new UrlEntity(formattedUrl, shortCode, expiresAt, createdAt);

        log.info("URL: {} will expire in {} minutes", formattedUrl, urlEntity.getMinutes());

        return urlRepository.save(urlEntity);
    }

    @Transactional
    public String getOriginalUrl(String shortCode) {
        Optional<UrlEntity> optionalUrl = urlRepository.findByShortCode(shortCode);

        if (optionalUrl.isEmpty()) {
            throw new UrlNotFoundException("Short code not found: " + shortCode);
        }

        UrlEntity urlEntity = optionalUrl.get();
        if (LocalDateTime.now().isAfter(urlEntity.getExpiresAt())) {
            urlRepository.delete(urlEntity);
            throw new UrlExpiredException("URL expired: " + shortCode);
        }

        log.info("Redirecting to: {}", urlEntity.getOriginalUrl());

        return urlEntity.getOriginalUrl();
    }

    @Scheduled(cron = "0 0 * * * ?")
    @Transactional
    public void removeExpiredUrls() {
        log.info("Removing all expired URLs");
        urlRepository.deleteAllExpiredSince(LocalDateTime.now());
    }
}
