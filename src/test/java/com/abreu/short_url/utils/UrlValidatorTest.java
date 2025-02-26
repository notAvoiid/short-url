package com.abreu.short_url.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class UrlValidatorTest {

    @InjectMocks
    private UrlValidator urlValidator;

    @BeforeEach
    void setUp() {
        Set<String> blacklistedDomains = Set.of("blocked.com", "malicious.com");
        ReflectionTestUtils.setField(urlValidator, "blacklistedDomains", blacklistedDomains);
    }

    @Test
    @DisplayName("Validate format of a valid URL without protocol")
    void testValidateFormat_ValidUrl_WithoutProtocol() {
        String url = "www.google.com";

        String result = urlValidator.validateFormat(url);
        assertEquals("http://www.google.com", result);
    }

    @Test
    @DisplayName("Validate format of a valid URL with HTTP protocol")
    void testValidateFormat_ValidUrl_WithHttp() {
        String url = "http://www.google.com";

        String result = urlValidator.validateFormat(url);
        assertEquals("http://www.google.com", result);
    }

    @Test
    @DisplayName("Validate format of a valid URL with HTTPS protocol")
    void testValidateFormat_ValidUrl_WithHttps() {
        String url = "https://www.google.com";

        String result = urlValidator.validateFormat(url);
        assertEquals("https://www.google.com", result);
    }

    @Test
    @DisplayName("Validate format of a blacklisted domain")
    void testValidateFormat_BlacklistedDomain() {
        String url = "blocked.com";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            urlValidator.validateFormat(url);
        });
        assertEquals("Blocked domain: " + url, exception.getMessage());
    }

    @Test
    @DisplayName("Validate format of an invalid URL with an invalid TLD")
    void testValidateFormat_InvalidUrl_InvalidTLD() {
        String url = "http://www.invalidtld.xy";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            urlValidator.validateFormat(url);
        });
        assertEquals("Invalid URL: domain must contain a '.com'", exception.getMessage());
    }

    @Test
    @DisplayName("Validate format of a URL with an invalid short subdomain")
    void testValidateFormat_InvalidSubdomain() {
        String url = "http://a.b.com";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            urlValidator.validateFormat(url);
        });
        assertEquals("Subdomain segment too short: a", exception.getMessage());
    }

    @Test
    @DisplayName("Validate format of a valid domain with a valid subdomain")
    void testValidateFormat_ValidDomainWithValidSubdomain() {
        String url = "http://subdomain.example.com";

        String result = urlValidator.validateFormat(url);
        assertEquals("http://subdomain.example.com", result);
    }
}
