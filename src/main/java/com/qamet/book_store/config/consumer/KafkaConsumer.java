package com.qamet.book_store.config.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaConsumer {

    @KafkaListener(id = "1",
            topics = "book-events-topic",
            groupId = "book-events-group",
            containerFactory = "myKafkaListenerContainerFactory")
    public void consumeBookEvents(String message) {
        log.info("book consumed : {}" , message);
    }
}
