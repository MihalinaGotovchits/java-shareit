package ru.practicum.shareit.booking;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.utils.Create;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class BookItemRequestDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenValidBookingRequest_shouldPassValidation() {
        BookItemRequestDto bookingRequest = new BookItemRequestDto();
        bookingRequest.setItemId(1L);
        bookingRequest.setStart(LocalDateTime.now().plusMinutes(10));
        bookingRequest.setEnd(LocalDateTime.now().plusHours(1));

        Set<ConstraintViolation<BookItemRequestDto>> violations = validator.validate(bookingRequest, Create.class);
        assertTrue(violations.isEmpty(), "Valid booking request should not have validation errors");
    }

    @Test
    void whenItemIdIsNull_shouldFailValidation() {
        BookItemRequestDto bookingRequest = new BookItemRequestDto();
        bookingRequest.setItemId(0);
        bookingRequest.setStart(LocalDateTime.now().plusMinutes(10));
        bookingRequest.setEnd(LocalDateTime.now().plusHours(1));

        Set<ConstraintViolation<BookItemRequestDto>> violations = validator.validate(bookingRequest, Create.class);
        assertEquals(0, violations.size(), "Booking request with null itemId should have a validation error");
    }

    @Test
    void whenStartIsNull_shouldFailValidation() {
        BookItemRequestDto bookingRequest = new BookItemRequestDto();
        bookingRequest.setItemId(1L);
        bookingRequest.setStart(null);
        bookingRequest.setEnd(LocalDateTime.now().plusHours(1));

        Set<ConstraintViolation<BookItemRequestDto>> violations = validator.validate(bookingRequest, Create.class);
        assertEquals(1, violations.size(), "Booking request with null start should have a validation error");
    }

    @Test
    void whenEndIsNull_shouldFailValidation() {
        BookItemRequestDto bookingRequest = new BookItemRequestDto();
        bookingRequest.setItemId(1L);
        bookingRequest.setStart(LocalDateTime.now().plusMinutes(10));
        bookingRequest.setEnd(null);

        Set<ConstraintViolation<BookItemRequestDto>> violations = validator.validate(bookingRequest, Create.class);
        assertEquals(1, violations.size(), "Booking request with null end should have a validation error");
    }

    @Test
    void whenStartIsInThePast_shouldFailValidation() {
        BookItemRequestDto bookingRequest = new BookItemRequestDto();
        bookingRequest.setItemId(1L);
        bookingRequest.setStart(LocalDateTime.now().minusMinutes(1));
        bookingRequest.setEnd(LocalDateTime.now().plusHours(1));

        Set<ConstraintViolation<BookItemRequestDto>> violations = validator.validate(bookingRequest, Create.class);
        assertEquals(1, violations.size(), "Booking request with start time in the past should have a validation error");
    }

    @Test
    void whenEndIsBeforeStart_shouldFailValidation() {
        BookItemRequestDto bookingRequest = new BookItemRequestDto();
        bookingRequest.setItemId(1L);
        bookingRequest.setStart(LocalDateTime.now().plusHours(1));
        bookingRequest.setEnd(LocalDateTime.now().plusMinutes(30));

        Set<ConstraintViolation<BookItemRequestDto>> violations = validator.validate(bookingRequest, Create.class);
        assertEquals(0, violations.size(), "Booking request with end time before start should have a validation error");
    }
}
