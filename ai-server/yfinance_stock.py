import pandas as pd
import yfinance as yf
from datetime import datetime, timedelta

ticker = "005930.KS"

start_date = datetime(2026, 2, 23)
end_date = datetime(2026, 3, 24)

df = yf.download(
    ticker,
    start=start_date.strftime("%Y-%m-%d"),
    end=(end_date + timedelta(days=1)).strftime("%Y-%m-%d"),
    interval="1d",
    auto_adjust=False,
    progress=False
)

# 멀티인덱스 컬럼 처리
if hasattr(df.columns, "nlevels") and df.columns.nlevels > 1:
    df.columns = df.columns.get_level_values(0)

df = df.reset_index()

# 컬럼명 정리
df = df.rename(columns={
    "Date": "date",
    "Open": "open",
    "High": "high",
    "Low": "low",
    "Close": "close",
    "Adj Close": "adj_close",
    "Volume": "volume"
})

# 날짜 형식 통일
df["date"] = pd.to_datetime(df["date"]).dt.strftime("%Y-%m-%d")

# 저장
df.to_csv("stock_daily.csv", index=False, encoding="utf-8-sig")

print(df.head())
print(df.tail())