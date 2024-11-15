
package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/requests")
public class ItemRequestController {
    private final ItemRequestService requestService;
    private static final String XSHARER = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDtoOut saveNewRequest(@RequestBody ItemRequestDtoIn requestDtoIn,
                                            @RequestHeader(XSHARER) Long userId) {
        return requestService.saveNewRequest(requestDtoIn, userId);
    }

    @GetMapping
    public List<ItemRequestDtoOut> getRequestsByRequestor(@RequestHeader(XSHARER) Long userId) {
        return requestService.getRequestsByRequestor(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoOut> getAllRequests(@RequestParam(defaultValue = "1") Integer from,
                                                  @RequestParam(defaultValue = "10") Integer size,
                                                  @RequestHeader(XSHARER) Long userId) {
        return requestService.getAllRequests(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoOut getRequestById(@PathVariable Long requestId) {
        return requestService.getRequestById(requestId);
    }
}
