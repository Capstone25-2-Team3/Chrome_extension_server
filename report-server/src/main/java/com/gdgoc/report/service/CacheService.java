package com.gdgoc.report.service;

import com.gdgoc.report.domain.cache.RefineCache;
import com.gdgoc.report.domain.cache.RefineCacheRepository;
import com.gdgoc.report.dto.CacheResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CacheService {

    private final RefineCacheRepository cacheRepository;

    /**
     * text_norm 기반 캐시 조회 (exact match)
     */
    @Transactional
    public CacheResponse lookup(String textNorm) {
        String keyHash = CommentService.sha256(textNorm);
        Optional<RefineCache> cached = cacheRepository.findByKeyHash(keyHash);

        if (cached.isPresent()) {
            RefineCache cache = cached.get();
            cache.recordHit();
            cacheRepository.save(cache);
            return new CacheResponse(true, cache);
        }
        return CacheResponse.miss();
    }
}
