FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/kartingrm-backend.jar app.jar
EXPOSE 8090
ENTRYPOINT ["java", "-jar", "app.jar"]