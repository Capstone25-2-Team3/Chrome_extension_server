package com.gdgoc.report.domain.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {

    Optional<Comment> findByHash(String hash);

    boolean existsByHash(String hash);
}
