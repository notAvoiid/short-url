package com.abreu.shorturl.services;

import com.abreu.shorturl.exceptions.ShortCodeGenerationException;
import com.abreu.shorturl.exceptions.UrlExpiredException;
import com.abreu.shorturl.exceptions.UrlNotFoundException;
import com.abreu.shorturl.models.UrlEntity;
import com.abreu.shorturl.repositories.UrlRepository;
import com.abreu.shorturl.utils.ShortCodeGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UrlService {

    private static final int MAX_RETRIES = 3;
    private static final int SHORT_CODE_LENGTH = 8;
    private static final List<String> BLACKLISTED_DOMAINS = List.of(
            "malicious.com", "spam.net"
    );

    private final UrlRepository urlRepository;

    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    @Transactional
    public UrlEntity shortenUrl(String originalUrl, int minutes) {
        validateUrl(originalUrl);

        String shortCode = generateUniqueShortCode();

        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime expiresAt = createdAt.plusMinutes(minutes);

        UrlEntity urlEntity = new UrlEntity();
        urlEntity.setOriginalUrl(originalUrl);
        urlEntity.setShortCode(shortCode);
        urlEntity.setCreatedAt(createdAt);
        urlEntity.setExpiresAt(expiresAt);

        return urlRepository.save(urlEntity);
    }


    @Transactional
    public String getOriginalUrl(String shortCode) {
        Optional<UrlEntity> optionalUrl = urlRepository.findByShortCode(shortCode);
        if (optionalUrl.isEmpty()) {
            throw new UrlNotFoundException("Short code não encontrado: " + shortCode);
        }

        UrlEntity urlEntity = optionalUrl.get();
        if (LocalDateTime.now().isAfter(urlEntity.getExpiresAt())) {
            urlRepository.delete(urlEntity);
            throw new UrlExpiredException("URL expirada: " + shortCode);
        }

        return urlEntity.getOriginalUrl();
    }

    private String generateUniqueShortCode() {
        int attempts = 0;
        String shortCode;

        do {
            shortCode = ShortCodeGenerator.generate(SHORT_CODE_LENGTH);
            attempts++;
        } while (urlRepository.existsByShortCode(shortCode) && attempts < MAX_RETRIES);

        if (attempts >= MAX_RETRIES) {
            throw new ShortCodeGenerationException("Falha ao gerar código único");
        }

        return shortCode;
    }

    @Transactional
    public void deleteExpiredUrl(String shortCode) {
        urlRepository.deleteByShortCode(shortCode);
    }

    private void validateUrl(String url) {
        try {
            URI uri = new URI(url);
            if (!uri.isAbsolute()) {
                throw new IllegalArgumentException("URL deve ser absoluta (incluir protocolo)");
            }
            if (uri.getHost() == null) {
                throw new IllegalArgumentException("Domínio inválido");
            }
            String domain = uri.getHost().toLowerCase();
            if (BLACKLISTED_DOMAINS.contains(domain)) {
                throw new IllegalArgumentException("Domínio bloqueado: " + domain);
            }
        } catch (URISyntaxException ex) {
            throw new IllegalArgumentException("URL inválida: " + ex.getMessage());
        }
    }
}
