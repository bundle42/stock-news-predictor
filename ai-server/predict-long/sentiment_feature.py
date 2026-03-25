import pandas as pd

# 파일 설정
input_file = "news_sentiment_final.csv"
output_file = "lstm_news_features.csv"

# 감정분석 결과 파일 읽기
df = pd.read_csv(input_file)

# 전처리
df["date"] = df["date"].astype(str).str.strip()
df["sentiment_score"] = pd.to_numeric(df["sentiment_score"], errors="coerce")
df["confidence"] = pd.to_numeric(df["confidence"], errors="coerce")
df["label"] = df["label"].astype(str).str.lower().str.strip()

# 날짜별 feature 생성
def make_daily_features(group):
    news_count = len(group)

    positive_count = (group["label"] == "positive").sum()
    negative_count = (group["label"] == "negative").sum()
    neutral_count = (group["label"] == "neutral").sum()

    return pd.Series({
        "news_count": news_count,
        "sentiment_mean": group["sentiment_score"].mean(),
        "sentiment_std": group["sentiment_score"].std(),
        "sentiment_sum": group["sentiment_score"].sum(),
        "positive_count": positive_count,
        "negative_count": negative_count,
        "neutral_count": neutral_count,
        "positive_ratio": positive_count / news_count if news_count > 0 else 0,
        "negative_ratio": negative_count / news_count if news_count > 0 else 0,
        "neutral_ratio": neutral_count / news_count if news_count > 0 else 0,
        "confidence_mean": group["confidence"].mean()
    })

# 날짜별 집계
feature_df = df.groupby("date").apply(make_daily_features).reset_index()

# NaN 처리
feature_df = feature_df.fillna(0)

# 저장
feature_df.to_csv(output_file, index=False, encoding="utf-8-sig")

print("저장 완료:", output_file)
print(feature_df.head())