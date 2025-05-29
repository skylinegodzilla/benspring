# Run this file to build a docker image inside docker using this project. Stage 1 builds the Artafact from the project code, Stage 2 builds the docker image from the artafact.
# rember to incroment the build number from run -> edit config -> Dockerfile.
# Stage 1: Build
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]