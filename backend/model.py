from pathlib import Path
from typing import List, Dict, Any

import torch
from transformers import (
    AutoTokenizer,
    AutoModelForSequenceClassification,
    TextClassificationPipeline,
)

# --------------------
# 기본 설정
# --------------------
BASE_DIR = Path(__file__).resolve().parent
MODEL_DIR = BASE_DIR / "data" / "model_files"
TOKENIZER_NAME = "monologg/kobert"
DEVICE = 0 if torch.cuda.is_available() else -1

# --------------------
# 모델 및 파이프라인
# --------------------
tokenizer = AutoTokenizer.from_pretrained(
    TOKENIZER_NAME,
    use_fast=False,
    trust_remote_code = True,
)
model = AutoModelForSequenceClassification.from_pretrained(
    MODEL_DIR,
    local_files_only=True,
    trust_remote_code = True,
)
pipe = TextClassificationPipeline(
    model=model,
    tokenizer=tokenizer,
    device=DEVICE,
    return_all_scores=True,
    function_to_apply="sigmoid",
)

# --------------------
# 배치 라벨 탐지 함수
# --------------------
def detect_labels_batch(texts: List[str], threshold: float = 0.5) -> List[Dict[str, Any]]:
    all_scores = pipe(texts)
    results = []

    for text, scores in zip(texts, all_scores):
        non_clean = [s for s in scores if s["label"] != "clean"]
        clean_entry = next((s for s in scores if s["label"] == "clean"), None)

        confident_labels = [s["label"] for s in non_clean if s["score"] >= threshold]

        if confident_labels:
            labels = confident_labels
        elif clean_entry and clean_entry["score"] >= threshold:
            labels = ["clean"]
        else:
            top = max(scores, key=lambda x: x["score"])
            labels = [top["label"]]

        results.append({
            "text": text,
            "labels": labels,
            "scores": scores,
        })

    return results


# --------------------
# 단일 문장 예측용 wrapper
# --------------------
def predict_profanity(
    sentence: str,
    threshold: float = 0.5,
) -> Dict[str, Any]:
    """
    FastAPI에서 호출할 메인 함수
    - input: 하나의 문장
    - output: 욕설 여부, confidence, 라벨 목록 등
    """
    results = detect_labels_batch([sentence], threshold=threshold)
    r = results[0]

    # '악플/욕설'이 포함돼 있는지
    is_profanity = int("악플/욕설" in r["labels"])

    # confidence: 악플/욕설 점수 or 가장 높은 점수
    score_dict = {s["label"]: s["score"] for s in r["scores"]}
    if "악플/욕설" in score_dict:
        confidence = score_dict["악플/욕설"]
    else:
        non_clean_scores = [v for k, v in score_dict.items() if k != "clean"]
        confidence = max(non_clean_scores) if non_clean_scores else 0.0

    return {
        "isProfanity": is_profanity,
        "highlighted": [],  
        "confidence": confidence,
        "labels": r["labels"],
        "rawScores": r["scores"],
    }