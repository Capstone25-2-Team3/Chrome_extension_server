# backend/model.py

from __future__ import annotations

from pathlib import Path
from typing import List, Dict, Any, Tuple

import torch
from transformers import (
    AutoTokenizer,
    AutoModelForSequenceClassification,
    TextClassificationPipeline,
)

# --------------------
# 경로 / 기본 설정
# --------------------
BASE_DIR = Path(__file__).resolve().parent
MODEL_DIR = BASE_DIR / "data" / "model_files"

# 학습에 사용한 베이스 모델 (코랩에서 monologg/kobert 사용했다고 했으니까)
TOKENIZER_NAME = "monologg/kobert"

# GPU 있으면 0, 없으면 CPU(-1)
DEVICE = 0 if torch.cuda.is_available() else -1

# 욕설/악플로 판단할 때 기준이 되는 라벨 이름
HATE_LABEL = "악플/욕설"
CLEAN_LABEL = "clean"

# --------------------
# 모델 & 토크나이저 로드
# --------------------
# 토크나이저는 원래 사용했던 베이스 모델 이름으로
tokenizer = AutoTokenizer.from_pretrained(
    TOKENIZER_NAME,
    use_fast=False,  # kobert 토크나이저는 fast 버전이 없는 경우가 많음
)

# fine-tuned 가중치(config.json + model.safetensors) 로드
model = AutoModelForSequenceClassification.from_pretrained(
    MODEL_DIR,
    local_files_only=True,  # EC2에서 인터넷 없이도 로드 가능
)

# inference 파이프라인
pipe = TextClassificationPipeline(
    model=model,
    tokenizer=tokenizer,
    device=DEVICE,
    return_all_scores=True,      # 모든 라벨의 score를 반환
    function_to_apply="sigmoid", # multi-label 이면 sigmoid 사용
)


# --------------------
# 내부 유틸: 라벨 결정 함수
# --------------------
def _detect_labels(
    text: str,
    threshold: float = 0.5,
) -> Tuple[List[str], List[Dict[str, Any]]]:
    """
    코랩에서 쓰던 detect_labels 로직을 그대로 옮긴 버전.
    - 전체 score 목록(scores)을 얻고
    - clean 라벨을 제외한 것들만 필터링
    - threshold 이상인 라벨이 있으면 그 라벨 리스트 반환
    - 없으면 그 중에서 score가 가장 큰 라벨 하나만 반환
    """
    scores = pipe(text)[0]  # [{"label": "...", "score": ...}, ...] 형태

    # clean 제거
    filtered_scores = [s for s in scores if s["label"] != CLEAN_LABEL]

    # threshold 이상인 라벨만
    confident_labels = [s["label"] for s in filtered_scores if s["score"] >= threshold]

    if confident_labels:
        return confident_labels, scores

    # threshold 이상이 없으면, clean 제외 라벨 중에서 최고 점수 하나
    top = max(filtered_scores, key=lambda x: x["score"])
    return [top["label"]], scores


# --------------------
# 외부에서 쓰는 함수: predict_profanity
# --------------------
def predict_profanity(
    sentence: str,
    threshold: float = 0.5,
) -> Dict[str, Any]:
    """
    크롬 익스텐션에서 쓸 최종 API 형태.
    반환 형식 예시:
    {
        "isProfanity": 1,
        "highlighted": [],          # 현재는 위치정보가 없어서 빈 리스트
        "confidence": 0.87,         # 악플/욕설 라벨의 score 또는 최대 score
        "labels": ["악플/욕설"],     # (선택) 문제 되는 라벨들
        "rawScores": [...],         # (선택) 전체 라벨 score
    }
    """
    labels, scores = _detect_labels(sentence, threshold=threshold)

    # 전체 라벨 dict를 label -> score 형태로 변환
    score_dict = {s["label"]: float(s["score"]) for s in scores}

    # 욕설/악플 라벨이 있는지 확인
    is_profanity = 1 if HATE_LABEL in labels else 0

    # confidence:
    #   1) 악플/욕설 라벨이 있으면 그 score
    #   2) 없으면 clean을 제외한 label 중 최댓값
    if HATE_LABEL in score_dict:
        confidence = score_dict[HATE_LABEL]
    else:
        # clean 제외 후 최대 score
        non_clean_scores = [
            v for k, v in score_dict.items() if k != CLEAN_LABEL
        ]
        confidence = max(non_clean_scores) if non_clean_scores else 0.0

    result = {
        "isProfanity": is_profanity,
        "highlighted": [],      # 나중에 토큰 단위 위치까지 뽑고 싶으면 여기 확장
        "confidence": confidence,
        # 아래 두 개는 디버깅/분석용으로 추가(원하면 빼도 됨)
        "labels": labels,
        "rawScores": scores,
    }

    return result


# # 스크립트로 직접 실행했을 때 간단 테스트용
# if __name__ == "__main__":
#     test_texts = [
#         "안녕하세요, 좋은 하루 되세요!",
#         "야이 씨발 병신 새끼야!",
#     ]
#     for t in test_texts:
#         print("TEXT:", t)
#         print(predict_profanity(t))
#         print("-" * 40)