package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {

    @Mock
    private ItemRequestRepository requestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private ItemRequestService requestService;

    private final User requestor = User.builder()
            .id(2L)
            .email("user2@mail.ru")
            .name("user2")
            .build();
    private final User user = User.builder()
            .id(1L)
            .email("user@mail.ru")
            .name("user")
            .build();
    private final ItemRequest request = ItemRequest.builder()
            .id(1L)
            .description("description")
            .requestor(requestor)
            .created(LocalDateTime.now())
            .build();
    private final Item item = Item.builder()
            .id(1L)
            .name("item")
            .description("nice item")
            .available(true)
            .owner(user)
            .request(request)
            .build();


    @Test
    void saveNewRequest() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(requestor));
        when(requestRepository.save(any())).thenReturn(request);

        final ItemRequestDtoOut actualRequest = requestService.saveNewRequest(
                new ItemRequestDtoIn("description"), 2L);

        Assertions.assertEquals(ItemRequestMapper.toItemRequestDtoOut(request), actualRequest);
    }

    @Test
    void getRequestsByRequestor_whenUserFound_thenSavedRequest() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(requestor));
        when(requestRepository.findAllByRequestorId(anyLong(), any())).thenReturn(List.of(request));
        when(itemRepository.findAllByRequestId(1L)).thenReturn(List.of(item));
        final ItemRequestDtoOut requestDtoOut = ItemRequestMapper.toItemRequestDtoOut(request);
        requestDtoOut.setItems(List.of(ItemMapper.toItemDtoOut(item)));

        List<ItemRequestDtoOut> actualRequests = requestService.getRequestsByRequestor(2L);

        Assertions.assertEquals(List.of(requestDtoOut), actualRequests);
    }

    @Test
    void getRequestsByRequestor_whenUserNotFound_thenThrownException() {
        when((userRepository).findById(3L)).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () ->
                requestService.getRequestsByRequestor(3L));
    }

    @Test
    void getRequestById() {
        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(request));
        when(itemRepository.findAllByRequestId(1L)).thenReturn(List.of(item));
        ItemRequestDtoOut requestDto = ItemRequestMapper.toItemRequestDtoOut(request);
        requestDto.setItems(List.of(ItemMapper.toItemDtoOut(item)));

        ItemRequestDtoOut actualRequest = requestService.getRequestById(1L);

        Assertions.assertEquals(requestDto, actualRequest);
    }
}
