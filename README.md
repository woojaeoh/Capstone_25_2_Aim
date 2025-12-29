# AIM (Analyst Information Management)

증권 애널리스트의 신뢰도를 평가하고 투자 인사이트를 제공하는 종합 정보 관리 시스템

## 프로젝트 개요

AIM은 증권 애널리스트들이 발행하는 리포트를 체계적으로 수집·분석하여, **AIMS Score**라는 독자적인 신뢰도 지표를 통해 애널리스트를 평가하는 백엔드 시스템입니다.

투자자들에게 다음과 같은 가치를 제공합니다:
- 애널리스트의 과거 예측 정확도 기반 신뢰도 평가
- 종목별 목표가 통계 및 추이 분석
- AI 기반 리포트 숨겨진 의견(Hidden Opinion) 분석
- 섹터별 투자 전망 및 트렌드 파악
<img width="1894" height="634" alt="스크린샷 2025-12-29 203933" src="https://github.com/user-attachments/assets/bfc20d98-6f54-4b3b-afec-5b0eb8bc920d" />

<img width="1825" height="647" alt="스크린샷 2025-12-29 205523" src="https://github.com/user-attachments/assets/6cab58d0-eda4-48fd-b07b-7b55d278287b" />

<img width="1814" height="646" alt="스크린샷 2025-12-29 205611" src="https://github.com/user-attachments/assets/89e0d8b5-be22-4480-a345-d6d227e4932c" />

<img width="1344" height="768" alt="스크린샷 2025-12-29 210144" src="https://github.com/user-attachments/assets/70da22c4-3ed0-4544-a6f7-617b3cbba82c" />

## 주요 기능

### 1. 애널리스트 평가 시스템
- **AIMS Score**: 목표가 예측 정확도, 의견 적중률 등을 종합한 신뢰도 점수
- **애널리스트 랭킹**: AIMS Score 기준 순위 제공
- **트렌딩 애널리스트**: 최근 7일 검색량 기반 인기 애널리스트
- 
<img width="824" height="527" alt="image" src="https://github.com/user-attachments/assets/17a02f6c-0450-4bed-87ff-81e08107d8c3" />

<img width="772" height="422" alt="image" src="https://github.com/user-attachments/assets/f67fda97-57a6-4a0a-8649-71b1f1b54a82" />

<img width="891" height="542" alt="image" src="https://github.com/user-attachments/assets/ae35f2fb-4ef6-4f91-a529-92a6becb7822" />


### 2. 종목 분석
- **목표가 통계**: 종목별 애널리스트 목표가 최대/평균/최소
- **상승여력 분석**: 현재가 대비 목표가 기반 상승 가능성 계산
- **컨센서스 의견**: 종목에 대한 애널리스트들의 종합 투자 의견
- **종가 변동 추이**: 시계열 주가 데이터 시각화
<img width="1063" height="513" alt="image" src="https://github.com/user-attachments/assets/1a26962b-0fa1-4f44-af6e-4a3b33044af9" />

<img width="729" height="248" alt="image" src="https://github.com/user-attachments/assets/bc5e00ef-5a24-41bf-adcd-210b018945e2" />

<img width="1157" height="461" alt="스크린샷 2025-12-29 232946" src="https://github.com/user-attachments/assets/ca7df17b-ad88-41e8-90b6-bed44faedc00" />

<img width="212" height="158" alt="스크린샷 2025-12-29 233223" src="https://github.com/user-attachments/assets/4c3935a4-55bb-4592-8dcb-681853140581" />


### 3. 리포트 분석
- **Surface Opinion**: 리포트에 명시된 공개 의견 (BUY/HOLD/SELL)
- **Hidden Opinion**: AI 모델 기반 숨겨진 의견 분석 (0.0 ~ 1.0 확률)
- **의견 변화 추적**: 애널리스트별 리포트 이력 및 의견 변경 분석


### 4. 섹터 분석
- **섹터별 매수 비율**: 각 섹터에 대한 BUY 의견 비율
- **섹터 랭킹**: 매수 비율 기준 투자 유망 섹터 제공

<img width="1854" height="904" alt="스크린샷 2025-12-29 234052" src="https://github.com/user-attachments/assets/9804f0cb-02d2-44fd-baea-fc6655ded437" />

<img width="1854" height="891" alt="스크린샷 2025-12-29 234530" src="https://github.com/user-attachments/assets/d416a3cc-3e4b-41b8-a620-843400339e20" />


### 5. 통합 대시보드
- TOP 3 신뢰도 애널리스트 (AIMS Score 기준)
- TOP 3 상승여력 종목 (목표가 기반)
- TOP 3 매수 섹터 (매수 의견 비율 기준)
- 실시간 검색 트렌드

<img width="832" height="531" alt="image" src="https://github.com/user-attachments/assets/275eefa3-c087-4803-b768-db18545dcd8f" />

## 기술 스택

### Frontend
- **React.JS** - 최신 리액트 버전

### AI
- **Pytorch** - 파이썬 학습 모델
- **FinBERT** - 금융사전 학습 모델 및 분류기 모델


### Backend
- **Java 21** - 최신 LTS 버전
- **Spring Boot 3.5.6** - 프레임워크
- **Spring Data JPA** - ORM 및 데이터 접근
- **Hibernate** - JPA 구현체

### Database
- **MySQL 8** - 관계형 데이터베이스
- **P6Spy** - SQL 쿼리 모니터링

### API & Documentation
- **RESTful API** - 표준 REST API 설계
- **SpringDoc OpenAPI (Swagger) 2.6.0** - API 문서 자동화

### Data Processing
- **OpenCSV 5.9** - CSV 데이터 파싱 및 임포트

### Development Tools
- **Lombok** - 보일러플레이트 코드 자동 생성
- **Gradle** - 빌드 및 의존성 관리

## 시스템 아키텍처

```
┌─────────────────┐
│   Frontend      │
│  (React.js)     │
└────────┬────────┘
         │ REST API
┌────────▼────────────────────────┐
│     Spring Boot Backend         │
│  ┌──────────────────────────┐  │
│  │   Controller Layer       │  │
│  ├──────────────────────────┤  │
│  │   Service Layer          │  │
│  │  - AIMS Score 계산       │  │
│  │  - Hidden Opinion 분석   │  │
│  ├──────────────────────────┤  │
│  │   Repository Layer       │  │
│  │   (Spring Data JPA)      │  │
│  └──────────────────────────┘  │
└────────┬────────────────────────┘
         │ JDBC
┌────────▼────────┐
│   MySQL DB      │
│  - Analyst      │
│  - Report       │
│  - Stock        │
│  - ClosePrice   │
└─────────────────┘
```

## 설치 및 실행

### 사전 요구사항
- Java 21 이상
- MySQL 8.0 이상
- Gradle 7.0 이상

### 실행 방법

1. **데이터베이스 설정**
```sql
CREATE DATABASE aim CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. **설정 파일 생성**
```bash
cp src/main/resources/application.yml.template src/main/resources/application.yml
```

3. **데이터베이스 연결 정보 수정** (`application.yml`)
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/aim
    username: your_username
    password: your_password
```

4. **애플리케이션 실행**
```bash
./gradlew bootRun
```

5. **API 문서 확인**
- Swagger UI: http://localhost:8080/swagger-ui/index.html

## 팀원

| 이름 | 역할 | 담당 업무 |
|------|------|-----------|
| **오재우** | Backend | 백엔드 시스템 설계 및 구현, REST API 개발 |
| **배성빈** | AI | Hidden Opinion 분석 모델 개발, AIMS Score 알고리즘 설계 |
| **최현승** | Frontend | 사용자 인터페이스 개발, API 연동 |

## 프로젝트 정보

- **프로젝트 기간**: 2025년 9월 ~ 2025년 12월
- **프로젝트 유형**: Capstone Design Project (Aim)
- **개발 환경**: React.js, Pytorch, Spring Boot 3.x, Java 21, MySQL 8

---

**2025 Capstone Design Team- A.I.M**
