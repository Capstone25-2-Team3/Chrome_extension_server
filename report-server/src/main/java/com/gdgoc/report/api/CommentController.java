package com.gdgoc.report.api;

import com.gdgoc.report.domain.comment.Comment;
import com.gdgoc.report.dto.CommentRequest;
import com.gdgoc.report.dto.CommentResponse;
import com.gdgoc.report.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * 댓글 등록 (중복이면 기존 반환)
     */
    @PostMapping
    public ResponseEntity<CommentResponse> register(@Valid @RequestBody CommentRequest request) {
        Comment comment = commentService.registerOrGet(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CommentResponse(comment));
    }

    /**
     * 댓글 조회
     */
    @GetMapping("/{commentId}")
    public ResponseEntity<CommentResponse> get(@PathVariable UUID commentId) {
        Comment comment = commentService.getById(commentId);
        return ResponseEntity.ok(new CommentResponse(comment));
    }
}
