# Etapa de construcción
FROM maven:3.8.7-openjdk-18 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Etapa de ejecución
FROM openjdk:18-jdk-slim
EXPOSE 8080
COPY --from=build /app/target/dockerized-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
