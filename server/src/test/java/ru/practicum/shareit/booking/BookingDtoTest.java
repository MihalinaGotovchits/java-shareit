package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class BookingDtoTest {

    @Test
    void builderShouldCreateBookingDto() {
        Long expectedId = 1L;
        LocalDateTime expectedStart = LocalDateTime.of(2024, 11, 8, 12, 30);
        LocalDateTime expectedEnd = LocalDateTime.of(2024, 11, 30, 12, 30);
        BookingStatus expectedStatus = BookingStatus.WAITING;
        Long expectedBookerId = 2L;

        BookingDto bookingDto = BookingDto.builder()
                .id(expectedId)
                .start(expectedStart)
                .end(expectedEnd)
                .status(expectedStatus)
                .bookerId(expectedBookerId)
                .build();

        assertEquals(expectedId, bookingDto.getId());
        assertEquals(expectedStart, bookingDto.getStart());
        assertEquals(expectedEnd, bookingDto.getEnd());
        assertEquals(expectedStatus, bookingDto.getStatus());
        assertEquals(expectedBookerId, bookingDto.getBookerId());
    }

    @Test
    void constructorShouldInitializeFields() {
        Long expectedId = 1L;
        LocalDateTime expectedStart = LocalDateTime.of(2024, 11, 8, 12, 30);
        LocalDateTime expectedEnd = LocalDateTime.of(2024, 11, 30, 12, 30);
        BookingStatus expectedStatus = BookingStatus.WAITING;
        Long expectedBookerId = 2L;

        BookingDto bookingDto = new BookingDto(expectedId, expectedStart, expectedEnd, expectedStatus, expectedBookerId);

        assertEquals(expectedId, bookingDto.getId());
        assertEquals(expectedStart, bookingDto.getStart());
        assertEquals(expectedEnd, bookingDto.getEnd());
        assertEquals(expectedStatus, bookingDto.getStatus());
        assertEquals(expectedBookerId, bookingDto.getBookerId());
    }

    @Test
    void toStringShouldReturnExpectedString() {
        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2024, 11, 8, 12, 30))
                .end(LocalDateTime.of(2024, 11, 30, 12, 30))
                .status(BookingStatus.WAITING)
                .bookerId(2L)
                .build();

        String expectedString = "BookingDto(id=1, start=2024-11-08T12:30, end=2024-11-30T12:30, status=WAITING, bookerId=2)";
        String actualString = bookingDto.toString();

        assertEquals(expectedString, actualString);
    }

    @Test
    void equalsShouldWorkProperly() {
        BookingDto bookingDto1 = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2024, 11, 8, 12, 30))
                .end(LocalDateTime.of(2024, 11, 30, 12, 30))
                .status(BookingStatus.WAITING)
                .bookerId(2L)
                .build();

        BookingDto bookingDto2 = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2024, 11, 8, 12, 30))
                .end(LocalDateTime.of(2024, 11, 30, 12, 30))
                .status(BookingStatus.WAITING)
                .bookerId(2L)
                .build();

        assertEquals(bookingDto1, bookingDto2);
    }

    @Test
    void hashCodeShouldWorkProperly() {
        BookingDto bookingDto1 = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2024, 11, 8, 12, 30))
                .end(LocalDateTime.of(2024, 11, 30, 12, 30))
                .status(BookingStatus.WAITING)
                .bookerId(2L)
                .build();

        BookingDto bookingDto2 = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2024, 11, 8, 12, 30))
                .end(LocalDateTime.of(2024, 11, 30, 12, 30))
                .status(BookingStatus.WAITING)
                .bookerId(2L)
                .build();

        assertEquals(bookingDto1.hashCode(), bookingDto2.hashCode());
    }
}
