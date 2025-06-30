# Usa una imagen base oficial de OpenJDK 21
FROM openjdk:21-jdk-slim

# Argumento para la ruta del JAR
ARG JAR_FILE=target/*.jar

# Establece el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copia el archivo JAR empaquetado al contenedor
COPY ${JAR_FILE} app.jar

# Expone el puerto en el que se ejecuta la aplicación
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java","-jar","/app/app.jar"]