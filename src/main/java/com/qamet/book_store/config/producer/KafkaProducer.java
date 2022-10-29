package com.qamet.book_store.config.producer;

import com.qamet.book_store.rest.dto.BookDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaProducer {

    private final KafkaTemplate<String, BookDTO> kafkaTemplate;

    public void send(String topic, BookDTO data) {
        log.info("data produced: {}" , data);

        Message<BookDTO> bookDTOMessage = MessageBuilder
                .withPayload(data)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .build();

        kafkaTemplate.send(bookDTOMessage);
    }
}
