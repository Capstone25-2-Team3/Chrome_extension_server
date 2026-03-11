package com.gdgoc.report.domain.inference;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ModelInferenceRepository extends JpaRepository<ModelInference, UUID> {

    List<ModelInference> findByComment_CommentIdOrderByCreatedAtDesc(UUID commentId);

    List<ModelInference> findByModelVersion(String modelVersion);
}
