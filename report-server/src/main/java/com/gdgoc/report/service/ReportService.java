package com.gdgoc.report.service;

import com.gdgoc.report.domain.comment.Comment;
import com.gdgoc.report.domain.report.UserReport;
import com.gdgoc.report.domain.report.UserReportRepository;
import com.gdgoc.report.dto.ReportRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final UserReportRepository reportRepository;
    private final CommentService commentService;

    @Transactional
    public UserReport createReport(ReportRequest req) {
        Comment comment = commentService.getById(req.getCommentId());

        UserReport report = UserReport.create(
                comment,
                req.getReporterUserId(),
                req.getReportReason(),
                req.getSuggestedLabels(),
                req.getSuggestedClean(),
                req.getCorrectedText()
        );
        return reportRepository.save(report);
    }

    public List<UserReport> getReportsByComment(UUID commentId) {
        return reportRepository.findByComment_CommentId(commentId);
    }

    public Page<UserReport> getReportsByReason(String reason, Pageable pageable) {
        return reportRepository.findByReportReason(reason, pageable);
    }

    public Page<UserReport> getAllReports(Pageable pageable) {
        return reportRepository.findAll(pageable);
    }

    public List<Object[]> getReportStats() {
        return reportRepository.countByReportReason();
    }
}
