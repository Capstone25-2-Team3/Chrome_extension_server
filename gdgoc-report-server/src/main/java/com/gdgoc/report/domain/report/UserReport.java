package com.gdgoc.report.domain.report;

import com.gdgoc.report.domain.comment.Comment;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "user_reports")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserReport {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "report_id")
    private UUID reportId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @Column(name = "reporter_user_id")
    private String reporterUserId;

    @Column(name = "report_reason", nullable = false, length = 64)
    private String reportReason;

    @Type(JsonType.class)
    @Column(name = "suggested_labels", columnDefinition = "jsonb")
    private List<String> suggestedLabels;

    @Column(name = "suggested_clean")
    private Boolean suggestedClean;

    @Column(name = "corrected_text", columnDefinition = "TEXT")
    private String correctedText;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public static UserReport create(Comment comment, String reporterUserId,
                                     String reportReason, List<String> suggestedLabels,
                                     Boolean suggestedClean, String correctedText) {
        UserReport r = new UserReport();
        r.comment = comment;
        r.reporterUserId = reporterUserId;
        r.reportReason = reportReason;
        r.suggestedLabels = suggestedLabels;
        r.suggestedClean = suggestedClean;
        r.correctedText = correctedText;
        return r;
    }
}
