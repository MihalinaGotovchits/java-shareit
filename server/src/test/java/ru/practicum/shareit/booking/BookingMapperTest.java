package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BookingMapperTest {
    @Test
    void toBookingDto_ShouldMapBookingToBookingDto() {
        Booking booking = mock(Booking.class);
        when(booking.getId()).thenReturn(1L);
        when(booking.getStart()).thenReturn(LocalDateTime.now().plusDays(1));
        when(booking.getEnd()).thenReturn(LocalDateTime.now().plusDays(2));
        when(booking.getStatus()).thenReturn(BookingStatus.WAITING);
        when(booking.getBooker()).thenReturn(mock(User.class));
        when(booking.getBooker().getId()).thenReturn(2L);


        BookingDto bookingDto = BookingMapper.toBookingDto(booking);

        assertEquals(1L, bookingDto.getId());
        assertEquals(booking.getStart(), bookingDto.getStart());
        assertEquals(booking.getEnd(), bookingDto.getEnd());
        assertEquals(2L, bookingDto.getBookerId());
        assertEquals(BookingStatus.WAITING, bookingDto.getStatus());
    }

    @Test
    void toBooking_ShouldUpdateBookingFromBookingDtoIn() {
        BookingDtoIn bookingDtoIn = BookingDtoIn.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        Booking booking = new Booking();

        Booking updatedBooking = BookingMapper.toBooking(bookingDtoIn, booking);

        assertEquals(bookingDtoIn.getStart(), updatedBooking.getStart());
        assertEquals(bookingDtoIn.getEnd(), updatedBooking.getEnd());
        assertEquals(BookingStatus.WAITING, updatedBooking.getStatus());
    }
}
