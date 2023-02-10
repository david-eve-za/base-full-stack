# Build Stage
FROM maven:3.8.2-openjdk-11-slim AS build
COPY . .
RUN mvn clean package -Pprod -DskipTests

# Package Stage
FROM openjdk:11-jre-slim
COPY --from=build target/*.jar app.jar
EXPOSE 8888
ENTRYPOINT ["java","-jar","/app.jar"]
