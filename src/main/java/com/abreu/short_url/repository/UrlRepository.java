package com.abreu.short_url.repository;

import com.abreu.short_url.models.UrlEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<UrlEntity, Long> {

    Optional<UrlEntity> findByShortCode(String shortCode);
    boolean existsByShortCode(String shortCode);
    void deleteByShortCode(String shortCode);

    @Transactional
    @Modifying
    @Query("DELETE FROM UrlEntity e WHERE e.expiresAt < :now")
    void deleteAllExpiredSince(@Param("now") LocalDateTime now);
}
