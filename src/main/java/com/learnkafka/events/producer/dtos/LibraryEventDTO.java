package com.learnkafka.events.producer.dtos;

import com.learnkafka.events.producer.enums.ELibraryEventTypes;

/**
 * @author juandiegoespinosasantos@gmail.com
 * @version Jul 11, 2023
 * @since 17
 */
public record LibraryEventDTO(Integer id,
                              ELibraryEventTypes type,
                              BookDTO book) {
}