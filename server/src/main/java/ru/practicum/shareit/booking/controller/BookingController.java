package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.utils.Create;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;
    private static final String XSHARER = "X-Sharer-User-Id";

    @GetMapping
    public List<BookingDtoOut> getAllByBooker(@RequestParam(defaultValue = "1") Integer from,
                                              @RequestParam(defaultValue = "10") Integer size,
                                              @RequestParam(name = "state", defaultValue = "ALL") String state,
                                              @RequestHeader(XSHARER) Long bookerId) {
        log.info("GET / ByBooker {}", bookerId);
        return bookingService.getAllByBooker(from, size, state, bookerId);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoOut getBookingById(@PathVariable Long bookingId, @RequestHeader(XSHARER) Long userId) {
        log.info("GET / bookings / {}", bookingId);
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping("/owner")
    public List<BookingDtoOut> getAllByOwner(@RequestParam(defaultValue = "1") Integer from,
                                             @RequestParam(defaultValue = "10") Integer size,
                                             @RequestParam(name = "state", defaultValue = "ALL") String state,
                                             @RequestHeader(XSHARER) Long ownerId) {
        log.info("GET / ByOwner / {}", ownerId);
        return bookingService.getAllByOwner(from, size, state, ownerId);
    }

    @PostMapping
    public BookingDtoOut saveBooking(@Validated(Create.class) @RequestBody BookingDtoIn bookingDtoIn,
                                     @RequestHeader(XSHARER) Long userId) {
        log.info("POST / bookings");
        return bookingService.saveNewBooking(bookingDtoIn, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoOut approve(@PathVariable Long bookingId, @RequestParam(name = "approved") Boolean isApprove,
                                 @RequestHeader(XSHARER) Long userId) {
        log.info("PATCH / bookings / {}", bookingId);
        return bookingService.approved(bookingId, isApprove, userId);
    }
}
