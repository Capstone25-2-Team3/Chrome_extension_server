package com.gdgoc.report.domain.label;

import com.gdgoc.report.domain.comment.Comment;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comment_labels_gold")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(CommentLabelGoldId.class)
public class CommentLabelGold {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @Id
    @Column(name = "label_key", length = 64)
    private String labelKey;

    @Column(nullable = false)
    private Short value = 0;

    @Column(nullable = false, length = 32)
    private String source = "user_vote";

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
