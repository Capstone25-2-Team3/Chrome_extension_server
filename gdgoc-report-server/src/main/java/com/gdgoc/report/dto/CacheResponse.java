package com.gdgoc.report.dto;

import com.gdgoc.report.domain.cache.RefineCache;
import lombok.Getter;

@Getter
public class CacheResponse {

    private final boolean hit;
    private final String refinedText;
    private final String source;
    private final Long hitCount;

    public CacheResponse(boolean hit, RefineCache cache) {
        this.hit = hit;
        if (cache != null) {
            this.refinedText = cache.getRefinedText();
            this.source = cache.getSource();
            this.hitCount = cache.getHitCount();
        } else {
            this.refinedText = null;
            this.source = null;
            this.hitCount = null;
        }
    }

    public static CacheResponse miss() {
        return new CacheResponse(false, null);
    }
}
