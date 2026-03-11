package com.gdgoc.report.api;

import com.gdgoc.report.domain.report.UserReport;
import com.gdgoc.report.dto.ReportRequest;
import com.gdgoc.report.dto.ReportResponse;
import com.gdgoc.report.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /**
     * 신고 접수
     */
    @PostMapping
    public ResponseEntity<ReportResponse> createReport(@Valid @RequestBody ReportRequest request) {
        UserReport report = reportService.createReport(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ReportResponse(report));
    }

    /**
     * 특정 댓글의 신고 목록
     */
    @GetMapping("/comment/{commentId}")
    public ResponseEntity<List<ReportResponse>> getByComment(@PathVariable UUID commentId) {
        List<ReportResponse> reports = reportService.getReportsByComment(commentId)
                .stream().map(ReportResponse::new).collect(Collectors.toList());
        return ResponseEntity.ok(reports);
    }

    /**
     * 전체 신고 목록 (페이징)
     */
    @GetMapping
    public ResponseEntity<Page<ReportResponse>> getAll(
            @RequestParam(required = false) String reason,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<ReportResponse> page;
        if (reason != null && !reason.isBlank()) {
            page = reportService.getReportsByReason(reason, pageable).map(ReportResponse::new);
        } else {
            page = reportService.getAllReports(pageable).map(ReportResponse::new);
        }
        return ResponseEntity.ok(page);
    }

    /**
     * 신고 사유별 통계
     */
    @GetMapping("/stats")
    public ResponseEntity<List<Map<String, Object>>> getStats() {
        List<Map<String, Object>> stats = reportService.getReportStats().stream()
                .map(row -> Map.<String, Object>of("reason", row[0], "count", row[1]))
                .collect(Collectors.toList());
        return ResponseEntity.ok(stats);
    }
}
