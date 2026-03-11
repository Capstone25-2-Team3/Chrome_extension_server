package com.gdgoc.report.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CommentRequest {

    @NotBlank
    private String platform;

    private String contentUrl;
    private String commentExternalId;

    @NotBlank
    private String textRaw;

    @NotBlank
    private String textNorm;

    private String lang = "ko";
}
