# Build stage

FROM bellsoft/liberica-openjdk-alpine:17 AS builder

WORKDIR /app

COPY . .

RUN ./gradlew clean build -x test
# Create Log Directory
RUN mkdir -p /var/log/app /var/log/app/error
RUN chmod 755 /var/log/app /var/log/app/error

ENV LOG_FILE_PATH=/var/log/app


# Run stage

FROM bellsoft/liberica-openjdk-alpine:17

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", "-jar","app.jar"]
