# Imagen base con Java (ajustada a tu versión de Java; JHipster suele usar 17 o 21)
FROM eclipse-temurin:21-jre

# El jar que vamos a copiar (cualquier .jar que esté en target/)
ARG JAR_FILE=target/*.jar

# Copiamos el jar al contenedor con el nombre app.jar
COPY ${JAR_FILE} app.jar

# Exponemos el puerto en el que corre Spring Boot
EXPOSE 8080

# Comando de arranque
ENTRYPOINT ["java", "-jar", "/app.jar"]
