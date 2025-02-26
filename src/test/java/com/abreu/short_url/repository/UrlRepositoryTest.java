package com.abreu.short_url.repository;

import com.abreu.short_url.models.UrlEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
class UrlRepositoryTest {

    @Autowired
    private UrlRepository urlRepository;

    @BeforeEach
    void setUp() {
        UrlEntity urlEntity = new UrlEntity();
        urlEntity.setCreatedAt(LocalDateTime.now());
        urlEntity.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        urlEntity.setShortCode("abc123");
        urlEntity.setOriginalUrl("https://example.com");
        urlRepository.saveAndFlush(urlEntity);
    }

    @Nested
    class FindByShortCode {
        @Test
        @DisplayName("findByShortCode returns URL entity when found")
        void findByShortCode_ReturnsUrlEntity_WhenFound() {
            Optional<UrlEntity> foundUrl = urlRepository.findByShortCode("abc123");
            assertThat(foundUrl).isPresent();
            assertThat(foundUrl.get().getOriginalUrl()).isEqualTo("https://example.com");
        }

        @Test
        @DisplayName("findByShortCode returns empty when not found")
        void findByShortCode_ReturnsEmpty_WhenNotFound() {
            Optional<UrlEntity> foundUrl = urlRepository.findByShortCode("nonexistent");
            assertThat(foundUrl).isNotPresent();
        }
    }

    @Nested
    class ExistsByShortCode {
        @Test
        @DisplayName("existsByShortCode returns true when URL entity exists")
        void existsByShortCode_ReturnsTrue_WhenExists() {
            boolean exists = urlRepository.existsByShortCode("abc123");
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("existsByShortCode returns false when URL entity does not exist")
        void existsByShortCode_ReturnsFalse_WhenNotFound() {
            boolean exists = urlRepository.existsByShortCode("nonexistent");
            assertThat(exists).isFalse();
        }
    }

    @Nested
    class DeleteByShortCode {
        @Test
        @DisplayName("deleteByShortCode removes the URL entity")
        void deleteByShortCode_RemovesEntity() {
            urlRepository.deleteByShortCode("abc123");

            Optional<UrlEntity> deletedUrl = urlRepository.findByShortCode("abc123");
            assertThat(deletedUrl).isNotPresent();

            boolean existsAfterDeletion = urlRepository.existsByShortCode("abc123");
            assertThat(existsAfterDeletion).isFalse();
        }

        @Test
        @DisplayName("deleteByShortCode does nothing when the shortcode is not found")
        void deleteByShortCode_DoesNothing_WhenShortcodeNotFound() {
            urlRepository.deleteByShortCode("nonexistent");

            Optional<UrlEntity> existingUrl = urlRepository.findByShortCode("abc123");
            assertThat(existingUrl).isPresent();

            Optional<UrlEntity> nonExistentUrl = urlRepository.findByShortCode("nonexistent");
            assertThat(nonExistentUrl).isNotPresent();
        }
    }

    @Nested
    class UniqueShortCode {

        @Test
        @DisplayName("Saving a unique shortCode persists the entity successfully")
        void saveUniqueShortCode_PersistsSuccessfully() {
            UrlEntity uniqueEntity = new UrlEntity();
            uniqueEntity.setShortCode("unique456");
            uniqueEntity.setOriginalUrl("https://unique.com");
            uniqueEntity.setCreatedAt(LocalDateTime.now());
            uniqueEntity.setExpiresAt(LocalDateTime.now().plusMinutes(5));

            UrlEntity savedEntity = urlRepository.saveAndFlush(uniqueEntity);
            assertThat(savedEntity.getId()).isNotNull();

            Optional<UrlEntity> foundEntity = urlRepository.findByShortCode("unique456");
            assertThat(foundEntity).isPresent();
            assertThat(foundEntity.get().getOriginalUrl()).isEqualTo("https://unique.com");
        }

        @Test
        @DisplayName("Updating an entity with a duplicate shortCode throws DataIntegrityViolationException")
        void updateEntity_WithDuplicateShortCode_ThrowsException() {
            UrlEntity newEntity = new UrlEntity();
            newEntity.setShortCode("unique789");
            newEntity.setOriginalUrl("https://another.com");
            newEntity.setCreatedAt(LocalDateTime.now());
            newEntity.setExpiresAt(LocalDateTime.now().plusMinutes(5));
            urlRepository.saveAndFlush(newEntity);

            newEntity.setShortCode("abc123");

            assertThrows(DataIntegrityViolationException.class, () -> {
                urlRepository.saveAndFlush(newEntity);
            });
        }

        @Test
        @DisplayName("Saving duplicate shortCode throws DataIntegrityViolationException")
        void saveDuplicateShortCode_ThrowsDataIntegrityViolationException() {
            assertTrue(urlRepository.existsByShortCode("abc123"));

            UrlEntity duplicate = new UrlEntity();
            duplicate.setShortCode("abc123");

            assertThrows(DataIntegrityViolationException.class, () -> {
                urlRepository.saveAndFlush(duplicate);
            });
        }
    }
}
