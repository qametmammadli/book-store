FROM maven:3.8.4-openjdk-11 AS builder

COPY . .

RUN mvn clean package -DskipTests

FROM openjdk:11-jdk-alpine

COPY --from=builder target/book_store-0.0.1-SNAPSHOT.jar book-store.jar

EXPOSE 8080

CMD ["java", "-jar", "book-store.jar"]
