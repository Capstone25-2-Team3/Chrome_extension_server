# FastAPI 기본 툴
from fastapi import FastAPI
from pydantic import BaseModel
from fastapi.middleware.cors import CORSMiddleware
from model import predict_profanity

app = FastAPI()

# CORS 설정 (크롬 익스텐션/웹 요청 허용)
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # 나중에 확장ID로 제한 가능: ["chrome-extension://확장ID"]
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# 입력 데이터 모델
class TextInput(BaseModel):
    sentence: str

# 예측 API
@app.post("/predict")
async def predict(input: TextInput):
    result = predict_profanity(input.sentence)
    return {
        "sentence": input.sentence,
        **result
    }

# (선택) 기본 루트 페이지 테스트용
@app.get("/")
def root():
    return {"message": "Profanity Detection API running successfully!"}