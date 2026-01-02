FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
COPY . .
RUN chmod +x gradlew && ./gradlew :adapter:bootJar -x test --parallel

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/modules/adapter/build/libs/*.jar app.jar
EXPOSE 9091
ENTRYPOINT ["java", "-jar", "app.jar"]