package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class BookingMapperTest {

    @Test
    void toBookingDtoOutTest() {
        User booker = User.builder()
                .id(1L)
                .name("Booker")
                .email("booker@mail.ru")
                .build();
        Item item = Item.builder()
                .id(2L)
                .name("Item")
                .description("Nice item")
                .available(true)
                .owner(booker).build();
        Booking booking = Booking.builder()
                .id(3L)
                .start(LocalDateTime.of(2024, 11, 8, 12, 30))
                .end(LocalDateTime.of(2024, 11, 30, 12, 30))
                .status(BookingStatus.APPROVED)
                .item(item)
                .booker(booker)
                .build();

        BookingDtoOut bookingDtoOut = BookingMapper.toBookingDtoOut(booking);

        assertEquals(booking.getId(), bookingDtoOut.getId());
        assertEquals(booking.getStart(), bookingDtoOut.getStart());
        assertEquals(booking.getEnd(), bookingDtoOut.getEnd());
        assertEquals(booking.getStatus(), bookingDtoOut.getStatus());
        assertEquals(ItemMapper.toItemDto(item), bookingDtoOut.getItem());
        assertEquals(UserMapper.toUserDtoShort(booker), bookingDtoOut.getBooker());
    }

    @Test
    void toBookingDto_ShouldMapBookingToBookingDto() {
        User booker = User.builder()
                .id(1L)
                .name("Booker")
                .email("booker@mail.ru")
                .build();
        Booking booking = Booking.builder()
                .id(3L)
                .start(LocalDateTime.of(2024, 11, 8, 12, 30))
                .end(LocalDateTime.of(2024, 11, 30, 12, 30))
                .status(BookingStatus.APPROVED)
                .booker(booker)
                .build();

        BookingDto bookingDto = BookingMapper.toBookingDto(booking);

        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(booking.getStart(), bookingDto.getStart());
        assertEquals(booking.getEnd(), bookingDto.getEnd());
        assertEquals(booking.getStatus(), bookingDto.getStatus());
        assertEquals(booker.getId(), bookingDto.getBookerId());
    }

    @Test
    void toBookingTest() {
        User booker = User.builder().id(1L).name("Booker").email("booker@mail.ru").build();
        Booking booking = Booking.builder()
                .id(3L)
                .start(LocalDateTime.of(2024, 11, 1, 12, 30))
                .end(LocalDateTime.of(2024, 11, 10, 12, 30))
                .status(BookingStatus.APPROVED)
                .booker(booker)
                .build();

        BookingDtoIn bookingDtoIn = BookingDtoIn.builder()
                .start(LocalDateTime.of(2024, 11, 8, 12, 30))
                .end(LocalDateTime.of(2024, 11, 30, 12, 30))
                .itemId(2L)
                .build();

        Booking updatedBooking = BookingMapper.toBooking(bookingDtoIn, booking);

        assertEquals(bookingDtoIn.getStart(), updatedBooking.getStart());
        assertEquals(bookingDtoIn.getEnd(), updatedBooking.getEnd());
        assertEquals(BookingStatus.WAITING, updatedBooking.getStatus());
    }
}
