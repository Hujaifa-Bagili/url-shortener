FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN ./mvnw dependency:resolve
COPY src src
RUN ./mvnw package -DskipTests
ENTRYPOINT ["java", "-jar", "target/urlshortener-0.0.1-SNAPSHOT.jar"]
