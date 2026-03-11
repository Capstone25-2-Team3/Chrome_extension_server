package com.gdgoc.report.domain.report;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.UUID;

public interface UserReportRepository extends JpaRepository<UserReport, UUID> {

    List<UserReport> findByComment_CommentId(UUID commentId);

    Page<UserReport> findByReportReason(String reportReason, Pageable pageable);

    @Query("SELECT r.reportReason, COUNT(r) FROM UserReport r GROUP BY r.reportReason ORDER BY COUNT(r) DESC")
    List<Object[]> countByReportReason();

    long countByComment_CommentId(UUID commentId);
}
