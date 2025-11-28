from fastapi import FastAPI
from pydantic import BaseModel
from fastapi.middleware.cors import CORSMiddleware
from model import detect_labels_batch
from refine import refine_text

app = FastAPI()

# CORS 설정
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

class TextsRequest(BaseModel):
    texts: list[str]

@app.post("/predict")
def predict(request: TextsRequest):
    raw_texts = request.texts

    # 1. 모델로 혐오 라벨 추론
    model_results = detect_labels_batch(raw_texts)

    # 2. 각 결과에서 텍스트 + 라벨을 추출하고 순화
    refined_results = []
    for item in model_results:
        original_text = item["text"]
        labels = item["labels"]

        refined = refine_text(original_text, labels)
        refined_results.append({
            "original": refined["original"],
            "refined": refined["refined"]
        })

    return refined_results

@app.get("/")
def root():
    return {"message": "Profanity Refinement API running"}