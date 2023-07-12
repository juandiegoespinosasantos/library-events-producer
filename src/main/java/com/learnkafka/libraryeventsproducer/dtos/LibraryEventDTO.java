package com.learnkafka.libraryeventsproducer.dtos;

import com.learnkafka.libraryeventsproducer.enums.ELibraryEventTypes;

/**
 * @author juandiegoespinosasantos@gmail.com
 * @version Jul 11, 2023
 * @since 17
 */
public record LibraryEventDTO(Integer libraryEventId,
                              ELibraryEventTypes libraryEventType,
                              BookDTO book) {
}