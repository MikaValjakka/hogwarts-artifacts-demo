# Vaihe 1: Build (Maven + JDK 21 builder)
FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Voit skipata testit buildissa nopeuttaaksesi (poista jos haluat testit CI:ssä)
RUN mvn clean package -DskipTests

# Vaihe 2: Extract layered JAR (optimoitu Spring Boot -tapa)
FROM eclipse-temurin:21-jre-alpine AS extractor
WORKDIR /app
ARG JAR_FILE=/app/target/*.jar
COPY --from=builder ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

# Vaihe 3: Final runtime image (kevyt JRE 21 alpine)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=extractor /app/dependencies/ ./
COPY --from=extractor /app/spring-boot-loader/ ./
COPY --from=extractor /app/snapshot-dependencies/ ./
COPY --from=extractor /app/application/ ./

# Render asettaa automaattisesti $PORT-ympäristömuuttujan → käytä sitä
EXPOSE ${PORT:-8080}
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]