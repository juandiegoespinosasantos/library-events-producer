package com.learnkafka.events.producer;

import com.google.gson.Gson;
import com.learnkafka.events.producer.dtos.LibraryEventDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author juandiegoespinosasantos@gmail.com
 * @version Jul 13, 2023
 * @since 17
 */
@Component
@Slf4j
public class LibraryEventsProducer {

    @Value("${spring.kafka.topic}")
    public String topic;

    private final KafkaTemplate<Integer, String> kafkaTemplate;

    @Autowired
    public LibraryEventsProducer(KafkaTemplate<Integer, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public CompletableFuture<SendResult<Integer, String>> sendLibraryEvent(final LibraryEventDTO libraryEvent) {
        Integer key = libraryEvent.id();
        String value = new Gson().toJson(libraryEvent);

        // 1. block call - get metadata about the kafka cluster
        // 2. send message happens - Returns a CompletableFuture
        return kafkaTemplate.send(topic, key, value)
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        handleFailure(key, value, throwable);
                        return;
                    }

                    handleSuccess(key, value, result);
                });
    }

    public SendResult<Integer, String> sendLibraryEvent2(final LibraryEventDTO libraryEvent)
            throws ExecutionException, InterruptedException {
        Integer key = libraryEvent.id();
        String value = new Gson().toJson(libraryEvent);

        // 1. block call - get metadata about the kafka cluster
        // 2. Block and wait until the message is sent to the Kafka
        SendResult<Integer, String> sendResult = kafkaTemplate.send(topic, key, value).get();
        handleSuccess(key, value, sendResult);

        return sendResult;
    }

    public CompletableFuture<SendResult<Integer, String>> sendLibraryEvent3(final LibraryEventDTO libraryEvent) {
        Integer key = libraryEvent.id();
        String value = new Gson().toJson(libraryEvent);

        ProducerRecord<Integer, String> producerRecord = buildProducerRecord(key, value);

        // 1. block call - get metadata about the kafka cluster
        // 2. send message happens - Returns a CompletableFuture
        return kafkaTemplate.send(producerRecord)
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        handleFailure(key, value, throwable);
                        return;
                    }

                    handleSuccess(key, value, result);
                });
    }

    private void handleSuccess(final Integer key, final String value, final SendResult<Integer, String> result) {
        log.info("Message sent successfully for key: {} and value: {}, partition is {}",
                key, value, (result.getRecordMetadata()).partition());
    }

    private void handleFailure(final Integer key, final String value, final Throwable th) {
        log.error("Error sending the message for key: {} and value: {}, the exception is {}",
                key, value, th.getMessage(), th);
    }

    private ProducerRecord<Integer, String> buildProducerRecord(final Integer key, final String value) {
        List<Header> recordHeaders = List.of(
                new RecordHeader("event-source", "scanner".getBytes())
        );

        return new ProducerRecord<>(topic, null, key, value, recordHeaders);
    }
}