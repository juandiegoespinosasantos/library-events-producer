package com.learnkafka.events.producer.controllers;

import com.learnkafka.events.producer.LibraryEventsProducer;
import com.learnkafka.events.producer.dtos.LibraryEventDTO;
import com.learnkafka.events.producer.util.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.CompletableFuture;

@ExtendWith(SpringExtension.class)
class LibraryEventsControllerUnitTest {

    @Mock
    private LibraryEventsProducer mockProducer;

    private LibraryEventsController controller;

    @BeforeEach
    void setUp() {
        controller = new LibraryEventsController(mockProducer);
    }

    @Test
    void postLibraryEventTest() {
        // Given
        LibraryEventDTO requestBody = TestUtil.libraryEventRecord();
        Mockito.when(mockProducer.sendLibraryEvent(requestBody)).thenReturn(new CompletableFuture<>());

        // When
        ResponseEntity<LibraryEventDTO> expected = ResponseEntity.status(HttpStatus.CREATED).body(requestBody);
        ResponseEntity<LibraryEventDTO> actual = controller.postLibraryEvent(requestBody);

        // Then
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected.getStatusCode(), actual.getStatusCode());
        Assertions.assertEquals(expected.getBody(), actual.getBody());
        Mockito.verify(mockProducer, Mockito.only()).sendLibraryEvent(requestBody);
    }
}