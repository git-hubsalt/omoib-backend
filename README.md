# Omoib (오늘 모 입지?)

AI 기반 코디 추천 & 가상 피팅 서비스

## 📝 프로젝트 소개

Omoib은 국민대학교 빅데이터 학회 D&A, 인공지능 동아리 X:AI와 웹 개발 동아리 WINK가 공동으로 진행한 프로젝트입니다. 사용자의 옷장에서 적절한 코디를 추천하고 가상 피팅 기능을 제공하는 웹앱 서비스입니다. Transformer와 생성형 AI를 활용하여 최고의 사용자 경험을 제공합니다. 이 프로젝트는 2024년 11월 D&X:W 연합 컨퍼런스에서 발표되었습니다.

[대학보도: 2024 제1회 D&X:W Conference 개최](https://cms.kookmin.ac.kr/bizon/news/cba/college.do?mode=view&articleNo=5922677&title=2024+%EC%A0%9C1%ED%9A%8C+D%26X%3AW+Conference+%EA%B0%9C%EC%B5%9C)

## 🛠 기술 스택

### Backend
- Java 17
- Spring Boot 3.3.2
- Spring Security
- Spring Data JPA
- MySQL
- H2 Database (개발용)

### AWS Services
- ECS Fargate
- Lambda
- SQS (Simple Queue Service)
- S3
- Parameter Store

### 기타
- JWT
- Swagger
- Docker

## 🔑 주요 기능

### 1. 옷장 관리
- 다양한 종류의 의류를 옷장과 위시리스트에 등록
- Amazon S3와 Pre-signed URL을 활용한 안전한 이미지 업로드 및 관리
- 의류 데이터 DB 저장 및 관리

### 2. 코디 추천
- 상황별 맞춤 코디 추천
- 특정 의류 아이템과 어울리는 스타일 추천
- SQS를 통한 AI 모델 추론 결과 전달

### 3. 가상 피팅
- 전신 사진 업로드를 통한 가상 피팅
- AWS Lambda를 활용한 마스킹 벡터 생성 및 활용

## 🚀 시작하기

### 필수 조건
- Java 17
- Docker
- AWS 계정 및 관련 서비스 설정

### 실행 방법

1. 프로젝트 클론
```bash
git clone https://github.com/your-repository/omoib-backend.git
```

2. 프로젝트 빌드
```bash
./gradlew build
```

3. Docker 이미지 빌드
```bash
docker build -t omoib-backend .
```

4. 애플리케이션 실행
```bash
docker run -p 8080:8080 omoib-backend
```

## 📦 시스템 아키텍처

- AWS 기반 클라우드 환경에서 운영
- ECS Fargate를 통한 컨테이너 관리
- Lambda와 SQS를 활용한 비동기 모델 처리
- S3를 통한 이미지 저장 및 관리

