package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class BookingService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    public BookingDtoOut saveNewBooking(BookingDtoIn bookingDtoIn, Long userId) {
        Item item = getItem(bookingDtoIn.getItemId());
        User booker = getUser(userId);
        if (!item.getAvailable()) {
            throw new ItemIsNotAvailableException("Вещь " + item.getName() + " недоступна для брони");
        }
        if (booker.getId().equals(item.getOwner().getId())) {
            throw new NotAvailableToBookOwnItemsException("Бронирование собственной вещи не доступно");
        }
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        bookingRepository.save(BookingMapper.toBooking(bookingDtoIn, booking));
        log.info("Вещь с Id {} забронирована. Id брони {}", item.getName(), booking.getId());
        return BookingMapper.toBookingDtoOut(booking);
    }

    public BookingDtoOut approved(Long bookingId, Boolean isApproved, Long userId) {
        Booking booking = getById(bookingId);
        Item item = getItem(booking.getItem().getId());
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ItemIsNotAvailableException("Вещь " + item.getName() + " уже забронирована");
        }
        if (!userId.equals(item.getOwner().getId())) {
            throw new IllegalApproveException("Подтвердить бронирование может только собственник вещи");
        }
        BookingStatus newBookingStatus = isApproved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        booking.setStatus(newBookingStatus);
        log.info("Бронирование с Id {} обновлено", bookingId);
        return BookingMapper.toBookingDtoOut(booking);
    }

    @Transactional(readOnly = true)
    public BookingDtoOut getBookingById(Long bookingId, Long userId) {
        log.info("Получение бронирования с Id {}", bookingId);
        Booking booking = getById(bookingId);
        User booker = booking.getBooker();
        User owner = getUser(booking.getItem().getOwner().getId());
        if (!(booker.getId().equals(userId)) && !(owner.getId().equals(userId))) {
            throw new NotBookerException("Только владелец может просматривать данное бронирование");
        }
        return BookingMapper.toBookingDtoOut(booking);
    }

    @Transactional(readOnly = true)
    public List<BookingDtoOut> getAllByBooker(Integer from, Integer size, String state, Long bookerId) {
        User booker = getUser(bookerId);
        List<Booking> bookings;
        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStatusException("Неизвестное состояние: UNSUPPORTED_STATE");
        }
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("start").descending());
        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findAllByBookerId(bookerId, pageable);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerIdAndStateCurrent(bookerId, pageable);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndStatePast(bookerId, pageable);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStateFuture(bookerId, pageable);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatus(bookerId, BookingStatus.WAITING, pageable);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatus(bookerId, BookingStatus.REJECTED,
                        pageable);
                break;
            default:
                throw new UnsupportedStatusException("Неизвестное состояние: UNSUPPORTED_STATE");
        }
        return bookings.stream().map(BookingMapper::toBookingDtoOut).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BookingDtoOut> getAllByOwner(Integer from, Integer size, String state, Long ownerId) {
        User owner = getUser(ownerId);
        List<Booking> bookings;
        BookingState bookingState;
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("start").descending());
        try {
            bookingState = BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStatusException("Неизвестное состояние: UNSUPPORTED_STATE");
        }
        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findAllByOwnerId(ownerId, pageable);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByOwnerIdAndStateCurrent(ownerId, pageable);
                break;
            case PAST:
                bookings = bookingRepository.findAllByOwnerIdAndStatePast(ownerId, pageable);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByOwnerIdAndStateFuture(ownerId, pageable);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByOwnerIdAndStatus(ownerId, BookingStatus.WAITING,
                        pageable);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByOwnerIdAndStatus(ownerId, BookingStatus.REJECTED, pageable);
            default:
                throw new UnsupportedStatusException("Неизвестное состояние: UNSUPPORTED_STATE");
        }
        return bookings.stream().map(BookingMapper::toBookingDtoOut).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Booking getById(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException("Бронирование с Id " + bookingId + " не найдено"));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с Id " + userId + " не найден"));
    }

    private Item getItem(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Вещь с Id " + itemId + "не найдена"));
    }
}
