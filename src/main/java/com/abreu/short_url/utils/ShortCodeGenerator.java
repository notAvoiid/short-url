package com.abreu.short_url.utils;

import com.abreu.shorturl.exceptions.ShortCodeGenerationException;
import com.abreu.shorturl.repository.UrlRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Setter
@Getter
public class ShortCodeGenerator {

    private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder().withoutPadding();

    @Value("${app.short-code.length:8}")
    private int shortCodeLength;

    @Value("${app.short-code.max-retries}")
    private int maxRetries;

    private final UrlRepository urlRepository;

    public String generateUniqueShortCode() {
        int attempts = 0;
        String shortCode;

        do {
            shortCode = ShortCodeGenerator.generate(shortCodeLength);
            attempts++;
        } while (urlRepository.existsByShortCode(shortCode) && attempts < maxRetries);

        if (attempts >= maxRetries) {
            throw new ShortCodeGenerationException("Failed to generate a unique code");
        }

        return shortCode;
    }

    public static String generate(int length) {
        String hash = base64Encoder.encodeToString(
                (UUID.randomUUID().toString() + System.nanoTime()).getBytes()
        );
        return encodeToBase62(hash, length);
    }

    public static String encodeToBase62(String input, int maxLength) {
        StringBuilder sb = new StringBuilder();
        byte[] bytes = input.getBytes();

        long number = 0;
        for (byte b : bytes) {
            number = (number << 8) + (b & 0xFF);
        }

        while (number > 0 && sb.length() < maxLength) {
            sb.insert(0, BASE62.charAt((int) (number % 62)));
            number /= 62;
        }

        while (sb.length() < maxLength) {
            sb.insert(0, BASE62.charAt(secureRandom.nextInt(62)));
        }

        return sb.substring(0, maxLength);
    }
}
