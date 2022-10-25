package com.qamet.book_store.config.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaProducer {

    private final KafkaTemplate<Long, Object> kafkaTemplate;

    public void send(String topic, Object data) {
        log.info("data produced: {}" , data);
        kafkaTemplate.send(topic, data);
    }
}
