FROM gradle:jdk21 AS builder
WORKDIR /app
COPY . .
RUN gradle shadowJar --no-daemon

FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=builder /app/build/libs/*-all.jar app.jar
EXPOSE 25565
CMD ["java","-jar","app.jar"]