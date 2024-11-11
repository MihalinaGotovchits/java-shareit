package ru.practicum.shareit.booking;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.utils.Create;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;
    private static final String XSHARER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> saveBooking(@RequestHeader(XSHARER) Long userId,
                                              @Validated(Create.class) @RequestBody BookItemRequestDto requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.saveBooking(userId, requestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@PathVariable Long bookingId,
                                          @RequestParam(name = "approved") Boolean isApproved,
                                          @RequestHeader(XSHARER) Long userId) {
        log.info("PATCH / bookings / {}", bookingId);
        return bookingClient.approve(bookingId, isApproved, userId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(XSHARER) Long userId,
                                             @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByBooker(@RequestHeader(XSHARER) Long bookerId,
                                                 @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
                                                 @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, bookerId={}, from={}, size={}", stateParam, bookerId, from, size);
        return bookingClient.getAllByBooker(from, size, state, bookerId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllByOwner(@RequestParam(defaultValue = "1") @PositiveOrZero Integer from,
                                                @RequestParam(defaultValue = "10") @Positive Integer size,
                                                @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
                                                @RequestHeader(XSHARER) Long ownerId) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("GET / ByOwner / {}", ownerId);
        return bookingClient.getAllByOwner(from, size, state, ownerId);

    }
}

