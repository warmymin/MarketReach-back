# MarketReach Backend

Spring Boot 기반의 마케팅 캠페인 관리 시스템 백엔드 API입니다.

## 기술 스택

- **Java 17**
- **Spring Boot 3.x**
- **Spring Data JPA**
- **PostgreSQL** (데이터베이스)
- **Maven**

## 프로젝트 구조

```
src/main/java/com/example/demo/
├── config/          # 설정 클래스
├── controller/      # REST API 컨트롤러
├── entity/         # JPA 엔티티
├── repository/     # 데이터 접근 계층
├── service/        # 비즈니스 로직
└── KtMarketReachApplication.java  # 메인 애플리케이션
```

## 주요 기능

- **캠페인 관리**: 캠페인 생성, 수정, 삭제, 조회
- **고객 관리**: 고객 정보 관리 및 조회
- **배송 관리**: 배송 상태 추적 및 관리
- **타겟팅**: 지역별 타겟팅 설정
- **통계**: 캠페인 성과 분석

## 실행 방법

### 1. 사전 요구사항

- Java 17 이상 설치
- Maven 설치 (또는 프로젝트에 포함된 Maven Wrapper 사용)
- PostgreSQL 설치 및 실행
- 데이터베이스 `marketreach` 생성

### 2. 프로젝트 실행

```bash
# Maven Wrapper 사용 (권장)
./mvnw spring-boot:run

# 또는 Maven이 전역 설치된 경우
mvn spring-boot:run
```

### 3. 애플리케이션 접속

- **API 서버**: http://localhost:8080
- **PostgreSQL 데이터베이스**: localhost:5432

## API 엔드포인트

### 캠페인 관리
- `GET /api/campaigns` - 캠페인 목록 조회
- `POST /api/campaigns` - 새 캠페인 생성
- `GET /api/campaigns/{id}` - 특정 캠페인 조회
- `PUT /api/campaigns/{id}` - 캠페인 수정
- `DELETE /api/campaigns/{id}` - 캠페인 삭제

### 고객 관리
- `GET /api/customers` - 고객 목록 조회
- `POST /api/customers` - 새 고객 생성
- `GET /api/customers/{id}` - 특정 고객 조회
- `PUT /api/customers/{id}` - 고객 정보 수정
- `DELETE /api/customers/{id}` - 고객 삭제

### 배송 관리
- `GET /api/deliveries` - 배송 목록 조회
- `POST /api/deliveries` - 새 배송 생성
- `GET /api/deliveries/{id}` - 특정 배송 조회
- `PUT /api/deliveries/{id}` - 배송 상태 수정

### 통계
- `GET /api/statistics/campaigns` - 캠페인 통계
- `GET /api/statistics/deliveries` - 배송 통계

## 데이터베이스

### 초기 데이터 설정

프로젝트 실행 시 자동으로 샘플 데이터가 생성됩니다:

```bash
# 추가 샘플 데이터 생성 (선택사항)
node create-sample-data.js
node create-100-customers.js
```

### 스키마 마이그레이션

필요한 경우 SQL 스크립트를 실행하여 스키마를 업데이트할 수 있습니다:

```bash
# PostgreSQL 스키마 마이그레이션
psql -U username -d database_name -f complete_schema_migration.sql
```

## 개발 환경 설정

### application.properties

PostgreSQL 데이터베이스 설정:

```properties
# PostgreSQL 데이터베이스 설정
spring.datasource.url=jdbc:postgresql://localhost:5432/marketreach
spring.datasource.driverClassName=org.postgresql.Driver
spring.datasource.username=postgres
spring.datasource.password=your_password

# JPA 설정
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# PostgreSQL 연결 풀 설정
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
```

## 빌드 및 배포

```bash
# JAR 파일 빌드
./mvnw clean package

# 빌드된 JAR 실행
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

## 문제 해결

### 포트 충돌
기본 포트 8080이 사용 중인 경우 `application.properties`에서 포트를 변경하세요:

```properties
server.port=8081
```

### 데이터베이스 연결 문제
PostgreSQL 데이터베이스가 제대로 시작되지 않는 경우 다음을 확인하세요:

1. PostgreSQL 서비스가 실행 중인지 확인
2. 데이터베이스 `marketreach`가 생성되었는지 확인
3. 사용자 권한이 올바른지 확인
4. 애플리케이션 로그를 확인

## 라이센스

이 프로젝트는 MIT 라이센스 하에 배포됩니다.
