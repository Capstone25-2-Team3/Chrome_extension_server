package com.gdgoc.report.domain.cache;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "refine_cache")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefineCache {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "cache_id")
    private UUID cacheId;

    @Column(name = "key_hash", nullable = false, unique = true, length = 64)
    private String keyHash;

    @Column(name = "text_norm", nullable = false, columnDefinition = "TEXT")
    private String textNorm;

    @Column(name = "refined_text", nullable = false, columnDefinition = "TEXT")
    private String refinedText;

    @Column(length = 8)
    private String lang = "ko";

    @Column(nullable = false, length = 16)
    private String source = "llm";

    @Column(name = "model_version", length = 64)
    private String modelVersion;

    @Column(name = "quality_score")
    private Float qualityScore;

    @Column(name = "hit_count", nullable = false)
    private Long hitCount = 0L;

    @Column(name = "last_hit_at")
    private LocalDateTime lastHitAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 캐시 히트 시 카운트 증가 + 타임스탬프 갱신
     */
    public void recordHit() {
        this.hitCount++;
        this.lastHitAt = LocalDateTime.now();
    }
}
