package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exception.NotBookerException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentDtoIn;
import ru.practicum.shareit.item.comment.dto.CommentDtoOut;
import ru.practicum.shareit.item.comment.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Slf4j
@Transactional
@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Transactional(readOnly = true)
    @Override
    public List<ItemDtoOut> getItemByOwner(Long userId) {
        log.info("Получение списка вещей пользователя с ID {}", userId);
        getUser(userId);
        List<Item> items = itemRepository.findAllByOwnerId(userId);
        return addBookingsAndCommentsForList(items);
    }

    @Transactional
    @Override
    public ItemDtoOut getItemById(Long itemId, Long userId) {
        log.info("Получение вещи c Id {}", itemId);
        return itemRepository.findById(itemId).map(item -> addBookingsAndComments(item, userId)).orElseThrow(() ->
                new NotFoundException("Вещь с Id " + itemId + " не найдена"));
    }

    @Override
    public ItemDtoOut save(ItemDtoIn itemDtoIn, Long userId) {
        log.info("Сохранение новой вещи {} Id пользователя {}", itemDtoIn.getName(), userId);
        if (itemDtoIn.getName() == null || itemDtoIn.getName().isBlank()) {
            throw new IllegalArgumentException();
        }
        if (itemDtoIn.getDescription() == null || itemDtoIn.getDescription().isBlank()) {
            throw new IllegalArgumentException();
        }
        if (itemDtoIn.getAvailable() == null) {
            throw new IllegalArgumentException();
        }
        User owner = new User();
        getUser(userId);
        owner.setId(userId);
        Item item = ItemMapper.toItem(itemDtoIn);
        item.setOwner(owner);
        return ItemMapper.toItemDtoOut(itemRepository.save(item));
    }

    @Override
    public CommentDtoOut saveComment(Long itemId, CommentDtoIn commentDtoIn, Long userId) {
        User user = getUser(userId);
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Вещь с Id " + itemId + " не найдена"));
        if (!bookingRepository.existsByBookerIdAndItemIdAndEndBefore(user.getId(), item.getId(), LocalDateTime.now())) {
            throw new NotBookerException("Пользователь с Id " + userId + " не пользовался вещью " + item.getName());
        }
        Comment comment = commentRepository.save(CommentMapper.toComment(commentDtoIn, item, user));
        return CommentMapper.toCommentDtoOut(comment);
    }

    @Override
    public ItemDtoOut update(Long itemId, ItemDtoIn itemDtoIn, Long userId) {
        log.info("Обновление вещи {} с Id {}", itemDtoIn.getName(), itemId);
        getUser(userId);
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Вещь с ID " + itemId + "не найдена"));
        String name = itemDtoIn.getName();
        String description = itemDtoIn.getDescription();
        Boolean available = itemDtoIn.getAvailable();
        if (item.getOwner().getId().equals(userId)) {
            if (name != null && !name.isBlank()) {
                item.setName(name);
            }
            if (description != null && !description.isBlank()) {
                item.setDescription(description);
            }
            if (available != null) {
                item.setAvailable(available);
            }
        } else {
            throw new NotOwnerException("Пользователь с ID " + userId + " не является владельцем " + name);
        }
        return ItemMapper.toItemDtoOut(item);
    }

    @Override
    public List<ItemDtoOut> getItemBySearch(String text) {
        log.info("Получение вещей по поиску {}", text);
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.search(text).stream().map(ItemMapper::toItemDtoOut).collect(toList());
    }

    private List<ItemDtoOut> addBookingsAndCommentsForList(List<Item> items) {
        LocalDateTime thisMoment = LocalDateTime.now();

        Map<Item, Booking> itemsWithLastBookings = bookingRepository
                .findByItemInAndStartLessThanEqualAndStatus(items, thisMoment,
                        BookingStatus.APPROVED, Sort.by(DESC, "end"))
                .stream()
                .collect(Collectors.toMap(Booking::getItem, Function.identity(), (o1, o2) -> o1));

        Map<Item, Booking> itemsWithNextBookings = bookingRepository
                .findByItemInAndStartAfterAndStatus(items, thisMoment,
                        BookingStatus.APPROVED, Sort.by(ASC, "end"))
                .stream()
                .collect(Collectors.toMap(Booking::getItem, Function.identity(), (o1, o2) -> o1));

        Map<Item, List<Comment>> itemsWithComments = commentRepository
                .findByItemIn(items, Sort.by(DESC, "created"))
                .stream()
                .collect(groupingBy(Comment::getItem, toList()));

        List<ItemDtoOut> itemDtoOuts = new ArrayList<>();
        for (Item item : items) {
            ItemDtoOut itemDtoOut = ItemMapper.toItemDtoOut(item);
            Booking lastBooks = itemsWithLastBookings.get(item);
            if (itemsWithLastBookings.size() > 0 && lastBooks != null) {
                itemDtoOut.setLastBooking(BookingMapper.toBookingDto(lastBooks));
            }
            Booking nextBooks = itemsWithNextBookings.get(item);
            if (itemsWithNextBookings.size() > 0 && nextBooks != null) {
                itemDtoOut.setNextBooking(BookingMapper.toBookingDto(nextBooks));
            }

            List<CommentDtoOut> commentDtoOuts = itemsWithComments.getOrDefault(item, Collections.emptyList())
                    .stream()
                    .map(CommentMapper::toCommentDtoOut)
                    .collect(toList());
            itemDtoOut.setComments(commentDtoOuts);

            itemDtoOuts.add(itemDtoOut);
        }
        return itemDtoOuts;
    }

    private ItemDtoOut addBookingsAndComments(Item item, Long userId) {
        ItemDtoOut itemDtoOut = ItemMapper.toItemDtoOut(item);

        LocalDateTime thisMoment = LocalDateTime.now();
        if (itemDtoOut.getOwner().getId().equals(userId)) {
            itemDtoOut.setLastBooking(bookingRepository
                    .findFirstByItemIdAndStartLessThanEqualAndStatus(itemDtoOut.getId(), thisMoment,
                            BookingStatus.APPROVED, Sort.by(DESC, "end"))
                    .map(BookingMapper::toBookingDto)
                    .orElse(null));

            itemDtoOut.setNextBooking(bookingRepository
                    .findFirstByItemIdAndStartAfterAndStatus(itemDtoOut.getId(), thisMoment,
                            BookingStatus.APPROVED, Sort.by(ASC, "end"))
                    .map(BookingMapper::toBookingDto)
                    .orElse(null));
        }

        itemDtoOut.setComments(commentRepository.findAllByItemId(itemDtoOut.getId())
                .stream()
                .map(CommentMapper::toCommentDtoOut)
                .collect(toList()));

        return itemDtoOut;
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с Id " + userId + " не найден"));
    }
}

