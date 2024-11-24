package ru.practicum.shareit.exception;

import lombok.Generated;

@Generated
public class WrongDateException extends RuntimeException {
    public WrongDateException(String message) {
        super(message);
    }
}
