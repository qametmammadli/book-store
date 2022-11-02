package com.qamet.book_store.config.producer;

import com.qamet.book_store.rest.dto.BookDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaProducer {

    private final KafkaTemplate<String, BookDTO> kafkaTemplate;

    public void send(String topic, BookDTO data) {
        log.info("data produced: {}", data);

        Message<BookDTO> bookDTOMessage = MessageBuilder
                .withPayload(data)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .build();

        ListenableFuture<SendResult<String, BookDTO>> listenableFuture = kafkaTemplate.send(bookDTOMessage);

        listenableFuture.addCallback(getListenableFutureCallback());
    }

    private ListenableFutureCallback<SendResult<String, BookDTO>> getListenableFutureCallback() {
        return new ListenableFutureCallback<>() {
            @Override
            public void onFailure(Throwable ex) {
                handleFailure(ex);
            }

            @Override
            public void onSuccess(SendResult<String, BookDTO> result) {
                handleSuccess(result);
            }
        };
    }

    private void handleFailure(Throwable ex) {
        log.error("Error occurred when produced the message and the exception is {}", ex.getMessage());
    }

    private void handleSuccess(SendResult<String, BookDTO> result) {
        log.info("Message was produced successfully for the key: {}, the value: {}, partition: {}, offset: {}",
                result.getProducerRecord().key(),
                result.getProducerRecord().value(),
                result.getRecordMetadata().partition(),
                result.getRecordMetadata().offset()
        );
    }
}
