package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private BookingRepository bookingRepository;

    private final User user = User.builder()
            .id(null)
            .email("user@mail.ru")
            .name("user")
            .build();
    private final User booker = User.builder()
            .id(null)
            .email("user2@mail.ru")
            .name("booker")
            .build();
    private final Item item = Item.builder()
            .id(null)
            .name("item")
            .description("cool")
            .available(true)
            .owner(user)
            .request(null)
            .build();
    private final Booking booking = Booking.builder()
            .id(1L)
            .start(LocalDateTime.of(2023, 7, 1, 12, 12, 12))
            .end(LocalDateTime.of(2023, 7, 30, 12, 12, 12))
            .item(item)
            .booker(booker)
            .status(BookingStatus.WAITING)
            .build();

    @BeforeEach
    void setUp() {
        userRepository.save(user);
        userRepository.save(booker);
        itemRepository.save(item);
        bookingRepository.save(booking);
    }

    @Test
    @DirtiesContext
    void findAllByBookerId() {
        List<Booking> bookings = bookingRepository.findAllByBookerId(booker.getId(), Pageable.ofSize(10));

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(booking.getId()));
        assertThat(bookings.get(0), equalTo(booking));
    }
}