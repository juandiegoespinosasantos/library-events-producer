package com.learnkafka.libraryeventsproducer.dtos;

/**
 * @author juandiegoespinosasantos@gmail.com
 * @version Jul 11, 2023
 * @since 17
 */
public record BookDTO(Integer id,
                      String name,
                      String author) {
}