package com.gdgoc.report.dto;

import com.gdgoc.report.domain.report.UserReport;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
public class ReportResponse {

    private final UUID reportId;
    private final UUID commentId;
    private final String reportReason;
    private final List<String> suggestedLabels;
    private final Boolean suggestedClean;
    private final String correctedText;
    private final LocalDateTime createdAt;

    public ReportResponse(UserReport report) {
        this.reportId = report.getReportId();
        this.commentId = report.getComment().getCommentId();
        this.reportReason = report.getReportReason();
        this.suggestedLabels = report.getSuggestedLabels();
        this.suggestedClean = report.getSuggestedClean();
        this.correctedText = report.getCorrectedText();
        this.createdAt = report.getCreatedAt();
    }
}
