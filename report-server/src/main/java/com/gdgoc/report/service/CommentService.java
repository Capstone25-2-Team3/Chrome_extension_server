package com.gdgoc.report.service;

import com.gdgoc.report.domain.comment.Comment;
import com.gdgoc.report.domain.comment.CommentRepository;
import com.gdgoc.report.dto.CommentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;

    public Comment getById(UUID commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다: " + commentId));
    }

    @Transactional
    public Comment registerOrGet(CommentRequest req) {
        String hash = sha256(req.getTextNorm());

        return commentRepository.findByHash(hash)
                .orElseGet(() -> {
                    Comment c = Comment.create(
                            req.getPlatform(),
                            req.getContentUrl(),
                            req.getCommentExternalId(),
                            req.getTextRaw(),
                            req.getTextNorm(),
                            req.getLang(),
                            hash
                    );
                    return commentRepository.save(c);
                });
    }

    public static String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
