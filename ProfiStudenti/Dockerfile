# Stadiul de build
FROM maven:3.9.4-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -Dmaven.test.skip=true

# Stadiul final (runtime)
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=builder /app/target/proiectPOS-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
