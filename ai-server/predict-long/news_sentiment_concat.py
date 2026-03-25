import pandas as pd

files = ["news_sentiment_20251124_20251221.csv", "news_sentiment_20251222_20260118.csv", "news_sentiment_20260119_20260222.csv", "news_sentiment_20260223_20260324.csv"]
dfs = [pd.read_csv(f) for f in files]

final_df = pd.concat(dfs, ignore_index=True)
final_df.to_csv("news_sentiment_final.csv", index=False)