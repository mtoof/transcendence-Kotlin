FROM openjdk:21-jdk-slim

WORKDIR /app

COPY build/libs/. /app/.

RUN chmod +x /app/user_service-0.0.1-SNAPSHOT.jar

CMD ["java", "-jar", "/app/user_service-0.0.1-SNAPSHOT.jar"]