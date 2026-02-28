from fastapi import FastAPI
from pydantic import BaseModel
from transformers import pipeline

app = FastAPI()

# 한국어 모델 추천 (처음 실행 시 다운로드됨) 서울대 NLP
sentiment_pipeline = pipeline(
    "text-classification",
    model="snunlp/KR-FinBert-SC"
)

@app.get("/")
def read_root():
    return {"message": "한국어 뉴스 감성 분석 API입니다. /analyze 엔드포인트로 POST 요청을 보내주세요."}

class NewsRequest(BaseModel):
    content: str

@app.post("/read")
def read_news(request: NewsRequest):
    return {"message": f"뉴스 내용이 수신되었습니다: {request.content[:100]}... (총 {len(request.content)}자)"}

@app.post("/analyze")
def analyze_sentiment(request: NewsRequest):
    text = request.content[:512]
    result = sentiment_pipeline(text)

    return {
        "label": result[0]["label"],
        "score": result[0]["score"]
    }