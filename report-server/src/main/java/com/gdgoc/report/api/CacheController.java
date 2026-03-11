package com.gdgoc.report.api;

import com.gdgoc.report.dto.CacheLookupRequest;
import com.gdgoc.report.dto.CacheResponse;
import com.gdgoc.report.service.CacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cache")
@RequiredArgsConstructor
public class CacheController {

    private final CacheService cacheService;

    /**
     * 순화 캐시 조회
     * POST로 하는 이유: text_norm이 길 수 있으므로 query param 부적합
     */
    @PostMapping("/lookup")
    public ResponseEntity<CacheResponse> lookup(@RequestBody CacheLookupRequest request) {
        CacheResponse response = cacheService.lookup(request.getTextNorm());
        return ResponseEntity.ok(response);
    }
}
