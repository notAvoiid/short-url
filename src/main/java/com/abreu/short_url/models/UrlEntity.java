package com.abreu.short_url.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "tb_urls")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UrlEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 2048)
    private String originalUrl;

    @Column(unique = true, nullable = false)
    private String shortCode;

    @Column(nullable = false)
    private LocalDateTime expiresAt;
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Transient
    public int getMinutes() {
        if (createdAt == null || expiresAt == null) {
            return 0;
        }
        return (int) ChronoUnit.MINUTES.between(createdAt, expiresAt);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(getExpiresAt());
    }

    public UrlEntity(String originalUrl, String shortCode, LocalDateTime expiresAt, LocalDateTime createdAt) {
        this.originalUrl = originalUrl;
        this.shortCode = shortCode;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
    }
}
