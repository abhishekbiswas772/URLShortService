FROM eclipse-temurin:17-jdk as build
LABEL authors="abhishekbiswas"
WORKDIR /workspace/app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN ./mvnw install -DskipTests

FROM eclipse-temurin:17-jre-focal
VOLUME /tmp
COPY --from=build /workspace/app/target/URLShortsDemo-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
