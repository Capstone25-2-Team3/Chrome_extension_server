package com.gdgoc.report.dto;

import com.gdgoc.report.domain.comment.Comment;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class CommentResponse {

    private final UUID commentId;
    private final String platform;
    private final String textNorm;
    private final String lang;
    private final LocalDateTime createdAt;

    public CommentResponse(Comment comment) {
        this.commentId = comment.getCommentId();
        this.platform = comment.getPlatform();
        this.textNorm = comment.getTextNorm();
        this.lang = comment.getLang();
        this.createdAt = comment.getCreatedAt();
    }
}
