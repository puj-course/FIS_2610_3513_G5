# Estructura del Dockerfile para el Backend de StudyHub

# Etapa 1: Construcción
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app

# Copiar el pom.xml y descargar dependencias (para aprovechar la caché de Docker)
COPY src/Back-end/pom.xml .
RUN mvn dependency:go-offline -B

# Copiar el código fuente
COPY src/Back-end/src ./src

# Copiar el frontend al directorio de recursos estáticos de Spring Boot
# Se usa la ruta completa para evitar ambigüedades
COPY src/Front-End/ /app/src/main/resources/static/

RUN mvn clean package -DskipTests -Dmaven.test.skip=true

# Etapa 2: Ejecución
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copiar todos los JARs y luego seleccionar el correcto (el más grande, que es el fat-jar)
COPY --from=build /app/target/*.jar ./
RUN mv $(ls -S *.jar | grep -v plain | head -n 1) app.jar

# Crear el directorio para carga de archivos
RUN mkdir -p uploads/fotos-perfil

# Exponer el puerto
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]