# FastAPI 기본 툴
from fastapi import FastAPI
from pydantic import BaseModel
from backend.model import predict_profanity

app = FastAPI()

class TextInput(BaseModel):
    sentence: str

@app.post("/predict")
async def predict(input: TextInput):
    result = predict_profanity(input.sentence)
    return {
        "sentence" : input.sentence, **result
    }