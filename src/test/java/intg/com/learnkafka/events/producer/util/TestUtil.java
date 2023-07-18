package com.learnkafka.events.producer.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnkafka.events.producer.dtos.BookDTO;
import com.learnkafka.events.producer.dtos.LibraryEventDTO;
import com.learnkafka.events.producer.enums.ELibraryEventTypes;

/**
 * @author juandiegoespinosasantos@gmail.com
 * @version Jul 17, 2023
 * @since 17
 */
public final class TestUtil {
    private TestUtil() {
    }

    public static BookDTO bookRecord() {
        return new BookDTO(123, "Dilip", "Kafka Using Spring Boot");
    }

    public static BookDTO bookRecordWithInvalidValues() {
        return new BookDTO(null, "", "Kafka Using Spring Boot");
    }

    public static LibraryEventDTO libraryEventRecord() {
        return new LibraryEventDTO(null, ELibraryEventTypes.NEW, bookRecord());
    }

    public static LibraryEventDTO newLibraryEventRecordWithLibraryEventId() {
        return new LibraryEventDTO(123, ELibraryEventTypes.NEW, bookRecord());
    }

    public static LibraryEventDTO libraryEventRecordUpdate() {
        return new LibraryEventDTO(123, ELibraryEventTypes.UPDATE, bookRecord());
    }

    public static LibraryEventDTO libraryEventRecordUpdateWithNullLibraryEventId() {
        return new LibraryEventDTO(null, ELibraryEventTypes.UPDATE, bookRecord());
    }

    public static LibraryEventDTO libraryEventRecordWithInvalidBook() {
        return new LibraryEventDTO(null, ELibraryEventTypes.NEW, bookRecordWithInvalidValues());
    }

    public static LibraryEventDTO parseLibraryEventRecord(ObjectMapper objectMapper, String json) {
        try {
            return objectMapper.readValue(json, LibraryEventDTO.class);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
    }
}