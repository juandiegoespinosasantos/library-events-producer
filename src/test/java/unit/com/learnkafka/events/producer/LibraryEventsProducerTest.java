package com.learnkafka.events.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.learnkafka.events.producer.dtos.LibraryEventDTO;
import com.learnkafka.events.producer.util.TestUtil;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@ExtendWith(SpringExtension.class)
class LibraryEventProducerUnitTest {

    private static final String TOPIC = "library-events";

    @Mock
    private KafkaTemplate<Integer, String> mockKafkaTemplate;

    private LibraryEventsProducer eventProducer;

    private final Gson gson = new Gson();

    @BeforeEach
    void setUp() {
        eventProducer = new LibraryEventsProducer(mockKafkaTemplate);

        ReflectionTestUtils.setField(eventProducer, "topic", TOPIC);
    }

    @Test
    @Disabled
    void sendLibraryEvent_Approach2_failure() throws JsonProcessingException, ExecutionException, InterruptedException {
        // Given
        CompletableFuture<SendResult<Integer, String>> completableFuture = getCompletableFuture()
                .thenApply((sendResult1) -> {
                    throw new RuntimeException("Exception Calling Kafka");
                });
        Mockito.when(mockKafkaTemplate.send(Mockito.isA(ProducerRecord.class)))
                .thenReturn(CompletableFuture.supplyAsync(() -> completableFuture));

        // When
        Exception ex = Assertions.assertThrows(Exception.class, eventProducer.sendLibraryEvent3(TestUtil.libraryEventRecord())::get);

        // Then
        Assertions.assertEquals("Exception Calling Kafka", ex.getMessage());
    }

    @Test
    void sendLibraryEvent_Approach3_success() throws JsonProcessingException, ExecutionException, InterruptedException {
        // Given
        CompletableFuture<SendResult<Integer, String>> future = getCompletableFuture();
        Mockito.when(mockKafkaTemplate.send(Mockito.isA(ProducerRecord.class))).thenReturn(future);

        // When
        CompletableFuture<SendResult<Integer, String>> actualCompletableFuture = eventProducer.sendLibraryEvent3(TestUtil.libraryEventRecord());

        // Then
        SendResult<Integer, String> actual = actualCompletableFuture.get();
        Assertions.assertEquals(1, actual.getRecordMetadata().partition());
    }

    private CompletableFuture<SendResult<Integer, String>> getCompletableFuture() {
        LibraryEventDTO libraryEvent = TestUtil.libraryEventRecord();
        String record = gson.toJson(libraryEvent);

        ProducerRecord<Integer, String> producerRecord = new ProducerRecord(TOPIC, libraryEvent.id(), record);
        RecordMetadata recordMetadata = new RecordMetadata(new TopicPartition(TOPIC, 1), 1, 1, System.currentTimeMillis(), 1, 2);
        SendResult<Integer, String> expected = new SendResult<>(producerRecord, recordMetadata);

        return CompletableFuture.supplyAsync(() -> expected);
    }
}