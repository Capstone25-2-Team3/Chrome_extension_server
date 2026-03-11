package com.gdgoc.report.domain.label;

import lombok.*;
import java.io.Serializable;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode
public class CommentLabelGoldId implements Serializable {

    private UUID comment;  // matches Comment.commentId type
    private String labelKey;
}
