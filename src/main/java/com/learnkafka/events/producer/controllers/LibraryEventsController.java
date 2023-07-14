package com.learnkafka.events.producer.controllers;

import com.learnkafka.events.producer.LibraryEventsProducer;
import com.learnkafka.events.producer.dtos.LibraryEventDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author juandiegoespinosasantos@gmail.com
 * @version Jul 11, 2023
 * @since 17
 */
@RestController
@RequestMapping("/v1/library-event")
@Slf4j
public class LibraryEventsController {

    private final LibraryEventsProducer producer;

    @Autowired
    public LibraryEventsController(LibraryEventsProducer producer) {
        this.producer = producer;
    }

    @PostMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LibraryEventDTO> postLibraryEvent(@RequestBody LibraryEventDTO requestBody) {
        log.info("libraryEvent: {}", requestBody);
        producer.sendLibraryEvent(requestBody);

        return ResponseEntity.status(HttpStatus.CREATED).body(requestBody);
    }
}