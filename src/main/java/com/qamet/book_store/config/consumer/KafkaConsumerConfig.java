package com.qamet.book_store.config.consumer;

import com.qamet.book_store.rest.dto.BookDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.NetworkException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    @Value("${spring.kafka.consumer.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Value("${topics.book.dead-letter}")
    private String deadLetter;

    private final KafkaTemplate<String, BookDTO> kafkaTemplate;

    public ConsumerFactory<String, BookDTO> consumerFactory() {

        var deserializer = new JsonDeserializer<>(BookDTO.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);

        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        return new DefaultKafkaConsumerFactory<>(configProps, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, BookDTO> myKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, BookDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setCommonErrorHandler(errorHandler());
        return factory;
    }

    public DefaultErrorHandler errorHandler() {
        ExponentialBackOffWithMaxRetries exponentialBackOff = new ExponentialBackOffWithMaxRetries(2); // total retries: 1 + 2
        exponentialBackOff.setInitialInterval(10_000);
        exponentialBackOff.setMultiplier(3);

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(dltPublishingRecoverer(), exponentialBackOff);

        setNonRetryableExceptions(errorHandler);
        setRetryableExceptions(errorHandler);
        logRetryListener(errorHandler);

        return errorHandler;
    }


    /**
     * when those exceptions (mentioned below) are thrown,
     * retry mechanism will retry same record for consuming
     */
    private void setRetryableExceptions(DefaultErrorHandler errorHandler) {
        errorHandler.addRetryableExceptions(
                NetworkException.class
        );
    }


    /**
     * will be triggered after exception (retryable exception) is thrown when try to consume
     */
    private void logRetryListener(DefaultErrorHandler errorHandler) {
        errorHandler.setRetryListeners((consumerRecord, ex, nthAttempt) ->
                log.info("Failed Record - Retry Listener data : {}, exception : {} , nthAttempt : {} ",
                        consumerRecord, ex.getMessage(), nthAttempt)
        );
    }


    /**
     * when those exceptions (mentioned below) are thrown,
     * retry mechanism will NOT retry same record for consuming
     */
    private void setNonRetryableExceptions(DefaultErrorHandler errorHandler) {
        errorHandler.addNotRetryableExceptions(
                NullPointerException.class
        );
    }


    public DeadLetterPublishingRecoverer dltPublishingRecoverer() {
        return new DeadLetterPublishingRecoverer(kafkaTemplate,
                (failedRecord, e) -> {
                    if (e.getCause() instanceof NetworkException) {
                        log.info("Network failure occurred in consuming message : {}", failedRecord.toString());
                        return new TopicPartition(failedRecord.topic(), failedRecord.partition());
                    }
                    log.info("Other failure occurred in consuming, move to dead letter topic, message : {}, exception : {}", failedRecord, e);
                    return new TopicPartition(deadLetter, failedRecord.partition());
                });
    }
}
