# stock-news-predictor

뉴스 데이터를 활용하여 특정 종목의 주가 상승/하락을 예측하는 웹 서비스입니다.  
뉴스 기사 수집, 감정 분석, 주가 데이터 결합을 통해 주가 방향성을 예측하는 것을 목표로 합니다.

---

## Project Overview

이 프로젝트는 다음과 같은 흐름으로 구성됩니다.

1. 네이버 뉴스 API를 통해 종목 관련 뉴스 수집
2. 뉴스 데이터를 MySQL 데이터베이스에 저장
3. 뉴스 본문 기반 감정 분석 수행
4. 뉴스 데이터와 주가 데이터를 결합하여 주가 상승/하락 예측
5. 예측 결과를 웹 화면으로 제공 (예정)

---

## Tech Stack

### Backend

- **Spring Boot**
- **Spring Data JPA**
- **MySQL**
- Gradle

### Frontend (Planned)

- **Next.js**

### Data & Analysis (Planned)

- News API (Naver)
- Stock price data
- Sentiment analysis
- Deep Learning (LSTM or similar)

---

## Project Structure
