FROM maven:3.8.2-jdk-11-slim AS build

RUN mkdir /project

COPY . /project

WORKDIR /project

RUN mvn clean package


FROM openjdk:11-jre-slim-buster

RUN mkdir /app

COPY --from=build /project/target/book_store-0.0.1-SNAPSHOT.jar /app/book-store.jar

WORKDIR /app

CMD ["java", "-jar", "-Dspring.profiles.active=docker", "book-store.jar"]

