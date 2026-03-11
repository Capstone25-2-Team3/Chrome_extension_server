package com.gdgoc.report.domain.cache;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface RefineCacheRepository extends JpaRepository<RefineCache, UUID> {

    Optional<RefineCache> findByKeyHash(String keyHash);
}
