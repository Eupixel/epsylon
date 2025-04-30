FROM gradle:8.8.0-jdk21-alpine
WORKDIR /app
COPY build/libs/epsylon-1.0.jar epsylon-1.0.jar
EXPOSE 25565
CMD ["java","-jar","epsylon-1.0.jar"]