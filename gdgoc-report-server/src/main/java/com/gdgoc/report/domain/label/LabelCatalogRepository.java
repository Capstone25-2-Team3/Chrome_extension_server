package com.gdgoc.report.domain.label;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LabelCatalogRepository extends JpaRepository<LabelCatalog, Integer> {

    Optional<LabelCatalog> findByLabelKey(String labelKey);
}
