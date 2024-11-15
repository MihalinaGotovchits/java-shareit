package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
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
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dto.UserDtoShort;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @InjectMocks
    private ItemServiceImpl itemService;

    private final long id = 1L;
    private final User user = User.builder()
            .id(id)
            .email("user@mail.ru")
            .name("user")
            .build();
    private final User notOwner = User.builder()
            .id(2L)
            .email("user2@mail.ru")
            .name("user2")
            .build();
    private final ItemDtoIn itemDtoIn = ItemDtoIn.builder()
            .name("item")
            .description("nice item")
            .available(true)
            .requestId(null)
            .build();
    private final ItemDtoOut itemDtoOut = ItemDtoOut.builder()
            .id(id)
            .name("item")
            .description("nice item")
            .available(true)
            .owner(new UserDtoShort(id, "user"))
            .build();
    private final Item item = Item.builder()
            .id(id)
            .name("item")
            .description("nice item")
            .available(true)
            .owner(user)
            .request(null)
            .build();
    private final CommentDtoOut commentDto = CommentDtoOut.builder()
            .id(id)
            .text("abc")
            .authorName("user")
            .created(LocalDateTime.of(2023, 7, 1, 12, 12, 12))
            .build();
    private final Comment comment = Comment.builder()
            .id(id)
            .text("abc")
            .item(item)
            .author(user)
            .created(LocalDateTime.of(2023, 7, 1, 12, 12, 12))
            .build();
    private final Booking booking = Booking.builder()
            .id(id)
            .start(null)
            .end(null)
            .item(item)
            .booker(user)
            .status(BookingStatus.WAITING)
            .build();

    @Test
    void saveNewItem_whenUserFound_thenSavedItem() {
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(itemRepository.save(any())).thenReturn(item);

        ItemDtoOut actualItemDto = itemService.save(itemDtoIn, id);

        Assertions.assertEquals(ItemMapper.toItemDtoOut(item), actualItemDto);
        Assertions.assertNull(item.getRequest());
    }

    @Test
    void saveNewItem_whenUserNotFound_thenNotSavedItem() {
        when((userRepository).findById(2L)).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> itemService.save(itemDtoIn, 2L));
    }

    @Test
    void saveNewItem_whenNoName_thenNotSavedItem() {
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        doThrow(DataIntegrityViolationException.class).when(itemRepository).save(any(Item.class));

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> itemService.save(itemDtoIn, id));
    }

    @Test
    void updateItem_whenUserIsOwner_thenUpdatedItem() {
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(itemRepository.findById(id)).thenReturn(Optional.of(item));

        ItemDtoOut actualItemDto = itemService.update(id, itemDtoIn, id);

        Assertions.assertEquals(itemDtoOut, actualItemDto);
    }

    @Test
    void updateItem_whenUserNotOwner_thenNotUpdatedItem() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(notOwner));
        when(itemRepository.findById(id)).thenReturn(Optional.of(item));

        Assertions.assertThrows(NotOwnerException.class, () -> itemService.update(id, itemDtoIn, 2L));
    }

    @Test
    void getItemById_whenItemFound_thenReturnedItem() {
        when(bookingRepository.findFirstByItemIdAndStartLessThanEqualAndStatus(anyLong(), any(), any(), any()))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.findFirstByItemIdAndStartAfterAndStatus(anyLong(), any(), any(), any()))
                .thenReturn(Optional.of(booking));
        when(commentRepository.findAllByItemId(id)).thenReturn(List.of(comment));
        when(itemRepository.findById(id)).thenReturn(Optional.of(item));
        final ItemDtoOut itemDto = ItemMapper.toItemDtoOut(item);
        itemDto.setLastBooking(BookingMapper.toBookingDto(booking));
        itemDto.setNextBooking(BookingMapper.toBookingDto(booking));
        itemDto.setComments(List.of(CommentMapper.toCommentDtoOut(comment)));

        ItemDtoOut actualItemDto = itemService.getItemById(id, id);

        Assertions.assertEquals(itemDto, actualItemDto);
    }

    @Test
    void getItemById_whenItemNotFound_thenExceptionThrown() {
        when((itemRepository).findById(2L)).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> itemService.getItemById(2L, id));
    }

    @Test
    void getItemsByOwner_CorrectArgumentsForPaging_thenReturnItems() {
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(itemRepository.findAllByOwnerId(anyLong(), any())).thenReturn(List.of(item));

        List<ItemDtoOut> targetItems = itemService.getItemByOwner(0, 10, id);

        Assertions.assertNotNull(targetItems);
        Assertions.assertEquals(1, targetItems.size());
        verify(itemRepository, times(1))
                .findAllByOwnerId(anyLong(), any());
    }

    @Test
    void getItemBySearch_whenTextNotBlank_thenReturnItems() {
        when(itemRepository.search(any(), any())).thenReturn(List.of(item));

        List<ItemDtoOut> targetItems = itemService.getItemBySearch(0, 10, "abc");

        Assertions.assertNotNull(targetItems);
        Assertions.assertEquals(1, targetItems.size());
        verify(itemRepository, times(1))
                .search(any(), any());
    }

    @Test
    void getItemBySearch_whenTextIsBlank_thenReturnEmptyList() {
        List<ItemDtoOut> targetItems = itemService.getItemBySearch(0, 10, "");

        Assertions.assertTrue(targetItems.isEmpty());
        Assertions.assertEquals(0, targetItems.size());
        verify(itemRepository, never()).search(any(), any());
    }

    @Test
    void saveNewComment_whenUserWasBooker_thenSavedComment() {
        when(bookingRepository.existsByBookerIdAndItemIdAndEndBefore(anyLong(), anyLong(), any()))
                .thenReturn(true);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(commentRepository.save(any())).thenReturn(comment);
        when(itemRepository.findById(id)).thenReturn(Optional.of(item));

        CommentDtoOut actualComment = itemService.saveComment(id, new CommentDtoIn("abc"), id);

        Assertions.assertEquals(commentDto, actualComment);
    }

    @Test
    void saveNewComment_whenUserWasNotBooker_thenThrownException() {
        Assertions.assertThrows(NotFoundException.class, () ->
                itemService.saveComment(2L, new CommentDtoIn("abc"), id));
    }

    @Test
    void saveNewItem_whenNameIsNull_thenThrowsIllegalArgumentException() {
        itemDtoIn.setName(null);

        Assertions.assertThrows(IllegalArgumentException.class, () -> itemService.save(itemDtoIn, id));
    }

    @Test
    void saveNewItem_whenDescriptionIsNull_thenThrowsIllegalArgumentException() {
        itemDtoIn.setDescription(null);

        Assertions.assertThrows(IllegalArgumentException.class, () -> itemService.save(itemDtoIn, id));
    }

    @Test
    void saveNewItem_whenAvailableIsNull_thenThrowsIllegalArgumentException() {
        itemDtoIn.setAvailable(null);

        Assertions.assertThrows(IllegalArgumentException.class, () -> itemService.save(itemDtoIn, id));
    }

    @Test
    void updateItem_whenOnlyDescriptionIsProvided_thenUpdatedOnlyDescription() {
        String newDescription = "Updated item description";
        itemDtoIn.setDescription(newDescription);
        itemDtoIn.setName(null);
        itemDtoIn.setAvailable(null);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(itemRepository.findById(id)).thenReturn(Optional.of(item));

        ItemDtoOut actualItemDto = itemService.update(id, itemDtoIn, id);

        Assertions.assertEquals(newDescription, actualItemDto.getDescription());
        Assertions.assertEquals(item.getName(), actualItemDto.getName());
        Assertions.assertEquals(item.getAvailable(), actualItemDto.getAvailable());
    }

    @Test
    void updateItem_whenAvailableIsUpdated_thenUpdateAvailability() {
        Boolean newAvailability = false;
        itemDtoIn.setAvailable(newAvailability);
        itemDtoIn.setName(null);
        itemDtoIn.setDescription(null);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(itemRepository.findById(id)).thenReturn(Optional.of(item));

        ItemDtoOut actualItemDto = itemService.update(id, itemDtoIn, id);

        Assertions.assertEquals(newAvailability, actualItemDto.getAvailable());
        Assertions.assertEquals(item.getName(), actualItemDto.getName());
        Assertions.assertEquals(item.getDescription(), actualItemDto.getDescription());
    }

    @Test
    void saveComment_whenUserWasNotBooker_thenThrowsNotBookerException() {
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(itemRepository.findById(id)).thenReturn(Optional.of(item));
        when(bookingRepository.existsByBookerIdAndItemIdAndEndBefore(anyLong(), anyLong(), any()))
                .thenReturn(false);

        Assertions.assertThrows(NotBookerException.class, () ->
                itemService.saveComment(id, new CommentDtoIn("abc"), id));
    }

    @Test
    void getItemBySearch_whenNoItemsFound_thenReturnEmptyList() {
        when(itemRepository.search(any(), any())).thenReturn(Collections.emptyList());

        List<ItemDtoOut> targetItems = itemService.getItemBySearch(0, 10, "nonexistent");

        Assertions.assertTrue(targetItems.isEmpty());
        Assertions.assertEquals(0, targetItems.size());
        verify(itemRepository, times(1)).search(any(), any());
    }

    @Test
    void getItemsByOwner_whenUserIsFoundButHasNoItems_thenReturnEmptyList() {
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(itemRepository.findAllByOwnerId(anyLong(), any())).thenReturn(Collections.emptyList());

        List<ItemDtoOut> targetItems = itemService.getItemByOwner(0, 10, id);

        Assertions.assertNotNull(targetItems);
        Assertions.assertTrue(targetItems.isEmpty());
        verify(itemRepository, times(1)).findAllByOwnerId(anyLong(), any());
    }

}
