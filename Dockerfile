FROM alpine:3.19.3

RUN apk add --no-cache openjdk11

RUN mkdir /app

COPY /target/book_store-0.0.1-SNAPSHOT.jar /app/book-store.jar

WORKDIR /app

CMD ["java", "-jar", "book-store.jar"]

