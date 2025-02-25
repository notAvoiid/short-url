FROM openjdk:17-jdk-slim
WORKDIR /app
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENV TZ=America/Sao_Paulo
ENTRYPOINT ["java", "-jar", "app.jar"]