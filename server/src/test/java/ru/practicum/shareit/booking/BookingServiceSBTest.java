package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.exception.ItemIsNotAvailableException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class BookingServiceSBTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private User user;
    private User owner;
    private Item item;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .name("User")
                .email("user@mail.com")
                .build();
        user = userRepository.save(user);

        owner = User.builder()
                .name("Owner")
                .email("owner@mail.com").build();
        owner = userRepository.save(owner);

        item = Item.builder()
                .name("Item")
                .description("Description")
                .available(true)
                .owner(owner)
                .build();
        item = itemRepository.save(item);
    }

    @Test
    @DirtiesContext
    void saveNewBooking_shouldSaveBooking() {
        BookingDtoIn bookingDtoIn = BookingDtoIn.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        BookingDtoOut bookingDtoOut = bookingService.saveNewBooking(bookingDtoIn, user.getId());

        assertNotNull(bookingDtoOut);
        assertEquals(item.getId(), bookingDtoOut.getItem().getId());
        assertEquals(user.getId(), bookingDtoOut.getBooker().getId());
        assertEquals(BookingStatus.WAITING, bookingDtoOut.getStatus());
    }


    @Test
    void saveNewBooking_itemUnavailable_shouldThrowException() {
        item.setAvailable(false);
        itemRepository.save(item);

        BookingDtoIn bookingDtoIn = BookingDtoIn.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        Exception exception = assertThrows(ItemIsNotAvailableException.class, () ->
                bookingService.saveNewBooking(bookingDtoIn, user.getId()));

        assertEquals("Вещь " + item.getName() + " недоступна для брони", exception.getMessage());
    }

    @Test
    void approved_shouldChangeBookingStatus() {
        BookingDtoIn bookingDtoIn = BookingDtoIn.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        BookingDtoOut bookingDtoOut = bookingService.saveNewBooking(bookingDtoIn, user.getId());

        BookingDtoOut approvedBooking = bookingService.approved(bookingDtoOut.getId(), true, owner.getId());

        assertEquals(BookingStatus.APPROVED, approvedBooking.getStatus());
    }

    @Test
    void getBookingById_shouldReturnBooking() {
        BookingDtoIn bookingDtoIn = BookingDtoIn.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        BookingDtoOut bookingDtoOut = bookingService.saveNewBooking(bookingDtoIn, user.getId());

        BookingDtoOut retrievedBooking = bookingService.getBookingById(bookingDtoOut.getId());

        assertEquals(bookingDtoOut.getId(), retrievedBooking.getId());
    }


    @Test
    void getAllByBooker_shouldReturnBookingList() {
        BookingDtoIn bookingDtoIn1 = BookingDtoIn.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        bookingService.saveNewBooking(bookingDtoIn1, user.getId());

        BookingDtoIn bookingDtoIn2 = BookingDtoIn.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(4))
                .build();

        bookingService.saveNewBooking(bookingDtoIn2, user.getId());

        List<BookingDtoOut> bookings = bookingService.getAllByBooker(0, 10, "ALL", user.getId());

        assertEquals(2, bookings.size());
    }


    @Test
    void getAllByOwner_shouldReturnBookingList() {
        BookingDtoIn bookingDtoIn = BookingDtoIn.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        bookingService.saveNewBooking(bookingDtoIn, user.getId());

        List<BookingDtoOut> bookings = bookingService.getAllByOwner(0, 10, "ALL", owner.getId());

        assertEquals(1, bookings.size());
    }
}

