package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.BookingRepository;
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
import static org.springframework.data.domain.Sort.Direction.DESC;

@DataJpaTest
class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository requestRepository;
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
    private final User requestor = User.builder()
            .id(null)
            .email("user2@mail.ru")
            .name("user2")
            .build();
    private final Item item = new Item(null, "item", "nice item", true, user, null);
    private final Booking booking = new Booking(1L,
            LocalDateTime.of(2023, 7, 1, 12, 12, 12),
            LocalDateTime.of(2023, 7, 30, 12, 12, 12),
            item, requestor, BookingStatus.WAITING);
    private final ItemRequest request = new ItemRequest(1L, "description", requestor, LocalDateTime.now());

    @BeforeEach
    void setUp() {
        userRepository.save(user);
        userRepository.save(requestor);
        itemRepository.save(item);
        bookingRepository.save(booking);
        requestRepository.save(request);
    }

    @Test
    @DirtiesContext
    void findAllByRequestorId() {
        List<ItemRequest> requests = requestRepository.findAllByRequestorId(2L, Sort.by(DESC, "created"));

        assertThat(requests.get(0).getId(), equalTo(request.getId()));
        assertThat(requests.size(), equalTo(1));
    }
}
