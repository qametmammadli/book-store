package com.qamet.book_store.config.consumer;

import com.qamet.book_store.rest.dto.BookDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaConsumer {

    @KafkaListener(id = "1",
            topics = "${topics.book.events}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "myKafkaListenerContainerFactory")
    public void consumeBookEvents(ConsumerRecord<String, BookDTO> messageRecord) {
        log.info("book consumed : {}", messageRecord);
    }
}
