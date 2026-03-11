package com.gdgoc.report.domain.label;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "label_catalog")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LabelCatalog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "label_id")
    private Integer labelId;

    @Column(name = "label_key", nullable = false, unique = true, length = 64)
    private String labelKey;

    @Column(name = "label_group", nullable = false, length = 16)
    private String labelGroup = "ko";
}
