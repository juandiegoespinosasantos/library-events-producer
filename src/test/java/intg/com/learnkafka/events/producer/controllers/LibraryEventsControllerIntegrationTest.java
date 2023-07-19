package com.learnkafka.events.producer.controllers;

import com.google.gson.Gson;
import com.learnkafka.events.producer.dtos.LibraryEventDTO;
import com.learnkafka.events.producer.util.TestUtil;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = {"library-events"})
@TestPropertySource(properties = {
        "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.admin.properties.bootstrap.servers=${spring.embedded.kafka.brokers}"})
public class LibraryEventsControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    private Consumer<Integer, String> consumer;

    @BeforeEach
    void setUp() {
        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.consumerProps("group1", "true", embeddedKafkaBroker));
        configs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        consumer = new DefaultKafkaConsumerFactory<>(configs, new IntegerDeserializer(), new StringDeserializer()).createConsumer();
        embeddedKafkaBroker.consumeFromAllEmbeddedTopics(consumer);
    }

    @AfterEach
    void tearDown() {
        consumer.close();
    }

    @Test
    void postLibraryEvent() {
        // Given
        LibraryEventDTO expected = TestUtil.libraryEventRecord();

        HttpHeaders headers = new HttpHeaders();
        headers.set("content-type", MediaType.APPLICATION_JSON.toString());
        HttpEntity<LibraryEventDTO> request = new HttpEntity<>(expected, headers);

        // When
        ResponseEntity<LibraryEventDTO> responseEntity = restTemplate.exchange("/v1/library-event/2",
                HttpMethod.POST, request, LibraryEventDTO.class);

        // Then
        Assertions.assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

        // Instantiate a consumer
        // Read the record , assert the count and parse the record and assert on it.

        ConsumerRecords<Integer, String> consumerRecords = KafkaTestUtils.getRecords(consumer);

        Assertions.assertEquals(1, consumerRecords.count());
        Gson gson = new Gson();

        consumerRecords.forEach(record -> {
            LibraryEventDTO actual = gson.fromJson(record.value(), LibraryEventDTO.class);
            Assertions.assertEquals(expected, actual);
        });
    }

    @Test
    void putLibraryEvent() {
        // Given
        LibraryEventDTO expected = TestUtil.libraryEventRecordUpdate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("content-type", MediaType.APPLICATION_JSON.toString());
        HttpEntity<LibraryEventDTO> request = new HttpEntity<>(expected, headers);

        // When
        ResponseEntity<LibraryEventDTO> responseEntity = restTemplate.exchange("/v1/library-event",
                HttpMethod.PUT, request, LibraryEventDTO.class);

        // Then
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Instantiate a consumer
        // Read the record , assert the count and parse the record and assert on it.

        ConsumerRecords<Integer, String> consumerRecords = KafkaTestUtils.getRecords(consumer);

        Assertions.assertEquals(1, consumerRecords.count());
        Gson gson = new Gson();

        consumerRecords.forEach(record -> {
            LibraryEventDTO actual = gson.fromJson(record.value(), LibraryEventDTO.class);
            Assertions.assertEquals(expected, actual);
        });
    }
}