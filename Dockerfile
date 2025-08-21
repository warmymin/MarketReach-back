# Spring Boot 애플리케이션을 위한 Dockerfile
FROM openjdk:17-jdk-slim

# 작업 디렉토리 설정
WORKDIR /app

# Maven Wrapper와 pom.xml 복사
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Maven Wrapper에 실행 권한 부여
RUN chmod +x ./mvnw

# 의존성 다운로드 (캐시 레이어 최적화)
RUN ./mvnw dependency:go-offline -B

# 소스 코드 복사
COPY src ./src

# 애플리케이션 빌드
RUN ./mvnw clean package -DskipTests

# 실행 가능한 JAR 파일만 복사
RUN cp target/*.jar app.jar

# 포트 노출
EXPOSE 8084

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
