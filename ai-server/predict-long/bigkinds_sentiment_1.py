import pandas as pd
from transformers import pipeline

# 파일 설정
input_file = "NewsResult_20251124-20251221.xlsx"
output_file = "news_sentiment_20251124_20251221.csv"

# 감정분석 모델 로드
sentiment_pipeline = pipeline(
    "text-classification",
    model="snunlp/KR-FinBert-SC"
)

# 엑셀 읽기
df = pd.read_excel(input_file)

# 전처리
df["일자"] = df["일자"].astype(str).str.strip()
df["제목"] = df["제목"].fillna("").astype(str).str.strip()

# 컬럼명 변경 + 날짜 형식 통일
df = df.rename(columns={
    "일자": "date",
    "제목": "title"
})
df["date"] = pd.to_datetime(df["date"], format="%Y%m%d", errors="coerce").dt.strftime("%Y-%m-%d")

# 결과 저장용 리스트
result_list = []

# 제목 감정분석 함수
def analyze_title(title):
    try:
        result = sentiment_pipeline(str(title)[:512])[0]
        label = result["label"]
        confidence = result["score"]

        if label == "positive":
            sentiment_score = confidence
        elif label == "negative":
            sentiment_score = -confidence
        else:
            sentiment_score = 0.0

        return label, confidence, sentiment_score

    except Exception as e:
        print("오류 발생:", title, e)
        return None, None, None

# 날짜별 처리
for date, group in df.groupby("date"):
    daily_rows = []

    for _, row in group.iterrows():
        label, confidence, sentiment_score = analyze_title(row["title"])

        daily_rows.append({
            "date": row["date"],
            "title": row["title"],
            "label": label,
            "confidence": confidence,
            "sentiment_score": sentiment_score
        })

    result_list.extend(daily_rows)

    # 날짜 하나 끝날 때마다 중간 저장
    temp_df = pd.DataFrame(result_list)
    temp_df.to_csv(output_file, index=False, encoding="utf-8-sig")

    print(f"{date} 완료 ({len(group)}건)")

# 최종 저장
result_df = pd.DataFrame(result_list)
result_df.to_csv(output_file, index=False, encoding="utf-8-sig")

print("최종 저장 완료:", output_file)
print(result_df.head())
print(result_df.tail())