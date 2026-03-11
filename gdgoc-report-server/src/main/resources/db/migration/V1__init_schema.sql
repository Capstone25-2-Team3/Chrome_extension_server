-- ============================================================
-- V1: 초기 스키마 - 신고/피드백 기반 학습 데이터셋 시스템
-- ============================================================

-- A. comments: 댓글 원본 + 정규화 텍스트
CREATE TABLE comments (
    comment_id      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    platform        VARCHAR(32)  NOT NULL,           -- youtube, community 등
    content_url     TEXT,                             -- 영상/게시글 URL
    comment_external_id TEXT,                         -- 플랫폼 댓글 ID
    text_raw        TEXT         NOT NULL,            -- 원문
    text_norm       TEXT         NOT NULL,            -- 전처리/정규화된 텍스트
    lang            VARCHAR(8)   DEFAULT 'ko',        -- ko/en/mixed
    hash            CHAR(64)     NOT NULL UNIQUE,     -- sha256(text_norm) 중복 제거
    created_at      TIMESTAMP    NOT NULL DEFAULT now()
);

CREATE INDEX idx_comments_hash ON comments(hash);
CREATE INDEX idx_comments_platform ON comments(platform);

-- B. model_inference: 모델 예측 결과 스냅샷
CREATE TABLE model_inference (
    infer_id        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    comment_id      UUID         NOT NULL REFERENCES comments(comment_id) ON DELETE CASCADE,
    model_version   VARCHAR(64)  NOT NULL,            -- kobert_v3 등
    labels_pred     JSONB        NOT NULL DEFAULT '{}', -- 예측 라벨 확률
    is_clean_pred   BOOLEAN      NOT NULL DEFAULT true,
    refined_text    TEXT,                              -- LLM 순화 결과
    latency_ms      INTEGER,                          -- 처리 시간(ms)
    created_at      TIMESTAMP    NOT NULL DEFAULT now()
);

CREATE INDEX idx_inference_comment ON model_inference(comment_id);
CREATE INDEX idx_inference_model ON model_inference(model_version);

-- C. user_reports: 사용자 신고/피드백
CREATE TABLE user_reports (
    report_id       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    comment_id      UUID         NOT NULL REFERENCES comments(comment_id) ON DELETE CASCADE,
    reporter_user_id TEXT,                             -- 익명화된 사용자 ID (선택)
    report_reason   VARCHAR(64)  NOT NULL,            -- 욕설 미탐, 과도 순화, 오탐 등
    suggested_labels JSONB       DEFAULT '[]',        -- 사용자 선택 라벨들
    suggested_clean BOOLEAN,                          -- 사용자 clean 여부 판단
    corrected_text  TEXT,                             -- 사용자 제안 순화 문장
    created_at      TIMESTAMP    NOT NULL DEFAULT now()
);

CREATE INDEX idx_reports_comment ON user_reports(comment_id);
CREATE INDEX idx_reports_reason ON user_reports(report_reason);

-- D-1. label_catalog: 라벨 마스터
CREATE TABLE label_catalog (
    label_id        SERIAL PRIMARY KEY,
    label_key       VARCHAR(64)  NOT NULL UNIQUE,     -- 여성/가족, toxic, clean 등
    label_group     VARCHAR(16)  NOT NULL DEFAULT 'ko' -- ko/en/common
);

-- 기본 라벨 시드 데이터
INSERT INTO label_catalog (label_key, label_group) VALUES
    ('여성/가족', 'ko'),
    ('남성', 'ko'),
    ('성소수자', 'ko'),
    ('인종/국적', 'ko'),
    ('연령', 'ko'),
    ('지역', 'ko'),
    ('종교', 'ko'),
    ('기타혐오', 'ko'),
    ('악플/욕설', 'ko'),
    ('clean', 'common'),
    ('toxic', 'en'),
    ('severe_toxic', 'en'),
    ('obscene', 'en'),
    ('insult', 'en'),
    ('threat', 'en'),
    ('identity_hate', 'en');

-- D-2. comment_labels_gold: 검수/합의된 최종 라벨
CREATE TABLE comment_labels_gold (
    comment_id      UUID         NOT NULL REFERENCES comments(comment_id) ON DELETE CASCADE,
    label_key       VARCHAR(64)  NOT NULL REFERENCES label_catalog(label_key),
    value           SMALLINT     NOT NULL DEFAULT 0 CHECK (value IN (0, 1)),
    source          VARCHAR(32)  NOT NULL DEFAULT 'user_vote', -- user_vote, admin, heuristic
    updated_at      TIMESTAMP    NOT NULL DEFAULT now(),
    PRIMARY KEY (comment_id, label_key)
);

-- E. refine_cache: 순화 결과 캐시
CREATE TABLE refine_cache (
    cache_id        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    key_hash        CHAR(64)     NOT NULL UNIQUE,     -- sha256(text_norm)
    text_norm       TEXT         NOT NULL,
    refined_text    TEXT         NOT NULL,
    lang            VARCHAR(8)   DEFAULT 'ko',
    source          VARCHAR(16)  NOT NULL DEFAULT 'llm', -- llm, manual, import
    model_version   VARCHAR(64),
    quality_score   REAL,
    hit_count       BIGINT       NOT NULL DEFAULT 0,
    last_hit_at     TIMESTAMP,
    created_at      TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP    NOT NULL DEFAULT now()
);

CREATE INDEX idx_cache_hash ON refine_cache(key_hash);
CREATE INDEX idx_cache_hit ON refine_cache(hit_count DESC);
