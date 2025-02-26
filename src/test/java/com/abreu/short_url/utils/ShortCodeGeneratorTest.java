package com.abreu.short_url.utils;

import com.abreu.short_url.exceptions.ShortCodeGenerationException;
import com.abreu.short_url.repository.UrlRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.abreu.short_url.utils.Constants.MAX_RETRIES;
import static com.abreu.short_url.utils.Constants.SHORT_CODE_LENGTH;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ShortCodeGeneratorTest {

    @Mock
    private UrlRepository urlRepository;

    @InjectMocks
    private ShortCodeGenerator shortCodeGenerator;

    @Test
    @DisplayName("Should return a unique code when the generated code is unique")
    void generateUniqueShortCode_WhenCodeIsUnique_ShouldReturnCode() {
        when(urlRepository.existsByShortCode(anyString())).thenReturn(false);

        shortCodeGenerator.setShortCodeLength(SHORT_CODE_LENGTH);
        shortCodeGenerator.setMaxRetries(MAX_RETRIES);

        String result = shortCodeGenerator.generateUniqueShortCode();

        assertNotNull(result);
        assertEquals(shortCodeGenerator.getShortCodeLength(), result.length());
        verify(urlRepository, times(1)).existsByShortCode(anyString());
    }

    @Test
    @DisplayName("Should throw an exception when the maximum number of retries is exceeded")
    void generateUniqueShortCode_WhenMaxRetriesExceeded_ShouldThrowException() {
        when(urlRepository.existsByShortCode(anyString())).thenReturn(true);

        shortCodeGenerator.setShortCodeLength(SHORT_CODE_LENGTH);
        shortCodeGenerator.setMaxRetries(MAX_RETRIES);

        assertThrows(ShortCodeGenerationException.class, () -> shortCodeGenerator.generateUniqueShortCode());
        verify(urlRepository, times(MAX_RETRIES)).existsByShortCode(anyString());
    }

    @Test
    @DisplayName("Should generate a code with the specified length")
    void generate_ShouldReturnCodeWithSpecifiedLength() {
        String result = ShortCodeGenerator.generate(SHORT_CODE_LENGTH);

        assertNotNull(result);
        assertEquals(SHORT_CODE_LENGTH, result.length());
    }

    @Test
    @DisplayName("Should correctly encode to Base62 with the specified length")
    void encodeToBase62_ShouldProduceCorrectLength() {
        String input = "testInput";
        int maxLength = 10;
        String result = ShortCodeGenerator.encodeToBase62(input, maxLength);

        assertNotNull(result);
        assertEquals(maxLength, result.length());
    }

    @Test
    @DisplayName("Should generate random characters when input is empty")
    void encodeToBase62_WithEmptyInput_ShouldUseRandomCharacters() {
        String input = "";
        int maxLength = 5;
        String result = ShortCodeGenerator.encodeToBase62(input, maxLength);

        assertNotNull(result);
        assertEquals(maxLength, result.length());
    }
}
