package com.gdgoc.report.domain.inference;

import com.gdgoc.report.domain.comment.Comment;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "model_inference")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ModelInference {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "infer_id")
    private UUID inferId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @Column(name = "model_version", nullable = false, length = 64)
    private String modelVersion;

    @Type(JsonType.class)
    @Column(name = "labels_pred", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> labelsPred;

    @Column(name = "is_clean_pred", nullable = false)
    private Boolean isCleanPred = true;

    @Column(name = "refined_text", columnDefinition = "TEXT")
    private String refinedText;

    @Column(name = "latency_ms")
    private Integer latencyMs;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
