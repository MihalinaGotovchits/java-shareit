package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingService bookingService;

    private final User user = User.builder()
            .id(1L)
            .email("user@mail.ru")
            .name("user")
            .build();
    private final User booker = User.builder()
            .id(2L)
            .email("booker@mail.ru")
            .name("booker")
            .build();
    private final Item item = Item.builder()
            .id(1L)
            .name("item")
            .description("nice item")
            .available(true)
            .owner(user)
            .request(null)
            .build();
    private final Booking booking = Booking.builder()
            .id(1L)
            .start(LocalDateTime.of(2024, 11, 8, 12, 30))
            .end(LocalDateTime.of(2024, 11, 30, 12, 30))
            .item(item)
            .booker(booker)
            .status(BookingStatus.WAITING)
            .build();
    private final BookingDtoIn bookingDtoIn = BookingDtoIn.builder()
            .start(LocalDateTime.of(2024, 11, 8, 12, 30))
            .end(LocalDateTime.of(2024, 11, 30, 12, 30))
            .itemId(1L)
            .build();
    private final BookingDtoIn bookingDtoWrongItem = BookingDtoIn.builder()
            .start(LocalDateTime.of(2024, 11, 8, 12, 30))
            .end(LocalDateTime.of(2024, 11, 30, 12, 30))
            .itemId(2L)
            .build();

    @Test
    void saveNewBooking_whenItemAvailable_thenSavedBooking() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingDtoOut actualBooking = bookingService.saveNewBooking(bookingDtoIn, 2L);

        Assertions.assertEquals(booking.getStart(), actualBooking.getStart());
        Assertions.assertEquals(booking.getEnd(), actualBooking.getEnd());
        Assertions.assertEquals(ItemMapper.toItemDto(booking.getItem()), actualBooking.getItem());
        Assertions.assertEquals(UserMapper.toUserDtoShort(booking.getBooker()), actualBooking.getBooker());
    }

    @Test
    void saveBooking_WhenUserNotFound_thenThrowException() {
        Assertions.assertThrows(NotFoundException.class, () ->
                bookingService.saveNewBooking(bookingDtoIn, 3L));
    }

    @Test
    void saveBooking_whenItemNotFound_thenThrowException() {
        when((itemRepository).findById(2L)).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () ->
                bookingService.saveNewBooking(bookingDtoWrongItem, 2L));
    }

    @Test
    void saveBooking_whenItemNotAvailable_thenThrowException() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        item.setAvailable(false);

        Assertions.assertThrows(ItemIsNotAvailableException.class, () ->
                bookingService.saveNewBooking(bookingDtoIn, 2L));
    }

    @Test
    void saveBooking_whenBookerIsOwner_thenThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        Assertions.assertThrows(NotAvailableToBookOwnItemsException.class, () ->
                bookingService.saveNewBooking(bookingDtoIn, 1L));
    }

    @Test
    void saveBooking_whenOwnerIsBooker_thenThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        Assertions.assertThrows(NotAvailableToBookOwnItemsException.class, () ->
                bookingService.saveNewBooking(bookingDtoIn, 1L));
    }

    @Test
    void approve() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        BookingDtoOut actualBooking = bookingService.approved(1L, true, 1L);

        Assertions.assertEquals(BookingStatus.APPROVED, actualBooking.getStatus());
    }

    @Test
    void approve_whenBookingNotFound_thenThrowException() {
        when((bookingRepository).findById(2L)).thenReturn(Optional.of(booking));

        Assertions.assertThrows(NotFoundException.class, () ->
                bookingService.approved(2L, true, 1L));
    }

    @Test
    void approve_whenItemAlreadyBooked_thenThrowException() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        booking.setStatus(BookingStatus.APPROVED);

        Assertions.assertThrows(NotFoundException.class, () ->
                bookingService.approved(1L, true, 1L));
    }

    @Test
    void getBookingById_whenUserIsOwner_thenReturnBooking() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        BookingDtoOut actualBooking = bookingService.getBookingById(1L);

        Assertions.assertEquals(BookingMapper.toBookingDtoOut(booking), actualBooking);
    }

    @Test
    void getAllByBooker_whenStateAll_thenReturnAllBookings() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerId(anyLong(), any())).thenReturn(List.of(booking));

        List<BookingDtoOut> actualBookings = bookingService.getAllByBooker(0, 10, "ALL", 2L);

        Assertions.assertEquals(List.of(BookingMapper.toBookingDtoOut(booking)), actualBookings);
    }

    @Test
    void getAllByBooker_whenStateCurrent_thenReturnAllBookings() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerIdAndStateCurrent(anyLong(), any())).thenReturn(List.of(booking));

        List<BookingDtoOut> actualBookings = bookingService.getAllByBooker(0, 10, "CURRENT", 2L);

        Assertions.assertEquals(List.of(BookingMapper.toBookingDtoOut(booking)), actualBookings);
    }

    @Test
    void getAllByBooker_whenStatePast_thenReturnAllBookings() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerIdAndStatePast(anyLong(), any())).thenReturn(List.of(booking));

        List<BookingDtoOut> actualBookings = bookingService.getAllByBooker(0, 10, "PAST", 2L);

        Assertions.assertEquals(List.of(BookingMapper.toBookingDtoOut(booking)), actualBookings);
    }

    @Test
    void getAllByBooker_whenStateFuture_thenReturnAllBookings() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerIdAndStateFuture(anyLong(), any())).thenReturn(List.of(booking));

        List<BookingDtoOut> actualBookings = bookingService.getAllByBooker(0, 10, "FUTURE", 2L);

        Assertions.assertEquals(List.of(BookingMapper.toBookingDtoOut(booking)), actualBookings);
    }

    @Test
    void getAllByBooker_whenStateWaiting_thenReturnAllBookings() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerIdAndStatus(anyLong(), any(), any())).thenReturn(List.of(booking));

        List<BookingDtoOut> actualBookings = bookingService.getAllByBooker(0, 10, "WAITING", 2L);

        Assertions.assertEquals(List.of(BookingMapper.toBookingDtoOut(booking)), actualBookings);
    }

    @Test
    void getAllByBooker_whenStateUnsupported_thenExceptionThrow() {
        Assertions.assertThrows(NotFoundException.class, () ->
                bookingService.getAllByBooker(0, 10, "a", 2L));
    }

    @Test
    void getAllByOwner_whenStateAll_thenReturnAllBookings() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByOwnerId(anyLong(), any())).thenReturn(List.of(booking));

        List<BookingDtoOut> actualBookings = bookingService.getAllByOwner(0, 10, "ALL", 1L);

        Assertions.assertEquals(List.of(BookingMapper.toBookingDtoOut(booking)), actualBookings);
    }

    @Test
    void getAllByOwner_whenStateCurrent_thenReturnAllBookings() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByOwnerIdAndStateCurrent(anyLong(), any())).thenReturn(List.of(booking));

        List<BookingDtoOut> actualBookings = bookingService.getAllByOwner(0, 10, "CURRENT", 1L);

        Assertions.assertEquals(List.of(BookingMapper.toBookingDtoOut(booking)), actualBookings);
    }

    @Test
    void getAllByOwner_whenStatePast_thenReturnAllBookings() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByOwnerIdAndStatePast(anyLong(), any())).thenReturn(List.of(booking));

        List<BookingDtoOut> actualBookings = bookingService.getAllByOwner(0, 10, "PAST", 1L);

        Assertions.assertEquals(List.of(BookingMapper.toBookingDtoOut(booking)), actualBookings);
    }

    @Test
    void getAllByOwner_whenStateFuture_thenReturnAllBookings() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByOwnerIdAndStateFuture(anyLong(), any())).thenReturn(List.of(booking));

        List<BookingDtoOut> actualBookings = bookingService.getAllByOwner(0, 10, "FUTURE", 1L);

        Assertions.assertEquals(List.of(BookingMapper.toBookingDtoOut(booking)), actualBookings);
    }
}