package com.gdgoc.report.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.UUID;

@Getter @Setter
public class ReportRequest {

    // -- 댓글 정보 (새 댓글이면 함께 등록) --
    @NotNull
    private UUID commentId;          // 기존 댓글 ID (없으면 아래 필드로 신규 생성)

    private String platform;
    private String contentUrl;
    private String commentExternalId;
    private String textRaw;
    private String textNorm;
    private String lang;

    // -- 신고 정보 --
    private String reporterUserId;

    @NotBlank(message = "신고 사유는 필수입니다")
    private String reportReason;     // 욕설 미탐, 과도 순화, 오탐, 다국어 우회

    private List<String> suggestedLabels;
    private Boolean suggestedClean;
    private String correctedText;
}
