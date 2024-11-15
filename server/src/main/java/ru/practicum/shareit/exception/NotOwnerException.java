package ru.practicum.shareit.exception;

import lombok.Generated;

@Generated
public class NotOwnerException extends RuntimeException {
    public NotOwnerException(String message) {
        super(message);
    }
}
