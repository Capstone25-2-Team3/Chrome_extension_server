package com.gdgoc.report.domain.comment;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "comments")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "comment_id")
    private UUID commentId;

    @Column(nullable = false, length = 32)
    private String platform;

    @Column(name = "content_url")
    private String contentUrl;

    @Column(name = "comment_external_id")
    private String commentExternalId;

    @Column(name = "text_raw", nullable = false, columnDefinition = "TEXT")
    private String textRaw;

    @Column(name = "text_norm", nullable = false, columnDefinition = "TEXT")
    private String textNorm;

    @Column(length = 8)
    private String lang = "ko";

    @Column(nullable = false, length = 64, unique = true)
    private String hash;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public static Comment create(String platform, String contentUrl,
                                  String commentExternalId, String textRaw,
                                  String textNorm, String lang, String hash) {
        Comment c = new Comment();
        c.platform = platform;
        c.contentUrl = contentUrl;
        c.commentExternalId = commentExternalId;
        c.textRaw = textRaw;
        c.textNorm = textNorm;
        c.lang = lang;
        c.hash = hash;
        return c;
    }
}
