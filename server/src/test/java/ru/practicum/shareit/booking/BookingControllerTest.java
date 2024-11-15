package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDtoShort;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mvc;

    private final BookingDtoIn bookingDtoIn = BookingDtoIn.builder()
            .start(LocalDateTime.now().plusDays(1))
            .end(LocalDateTime.now().plusDays(2))
            .itemId(1L)
            .build();
    private final BookingDtoIn bookingNullStart = BookingDtoIn.builder()
            .start(null)
            .end(LocalDateTime.now().plusDays(2))
            .itemId(1L)
            .build();
    private final BookingDtoOut bookingDtoOut = new BookingDtoOut(
            1L,
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(2),
            new ItemDto(1L, "item"),
            new UserDtoShort(1L, "user"),
            null);

    @Test
    void saveBooking() throws Exception {
        when(bookingService.saveNewBooking(any(), anyLong())).thenReturn(bookingDtoOut);

        mvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingDtoOut)));
    }

    @Test
    void approve() throws Exception {
        when(bookingService.approved(anyLong(), any(), anyLong())).thenReturn(bookingDtoOut);
        bookingDtoOut.setStatus(BookingStatus.APPROVED);

        mvc.perform(patch("/bookings/1?approved=true")
                        .content(objectMapper.writeValueAsString(bookingDtoOut))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingDtoOut)));
    }

    @Test
    void getBookingById() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong())).thenReturn(bookingDtoOut);

        mvc.perform(get("/bookings/1")
                        .content(objectMapper.writeValueAsString(bookingDtoOut))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingDtoOut)));
    }

    @Test
    void getAllByBooker() throws Exception {
        when(bookingService.getAllByBooker(anyInt(), anyInt(), anyString(), anyLong()))
                .thenReturn(List.of(bookingDtoOut));

        mvc.perform(get("/bookings?state=ALL")
                        .content(objectMapper.writeValueAsString(bookingDtoOut))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(bookingDtoOut))));
    }

    @Test
    void getAllByOwner() throws Exception {
        when(bookingService.getAllByOwner(anyInt(), anyInt(), anyString(), anyLong()))
                .thenReturn(List.of(bookingDtoOut));

        mvc.perform(get("/bookings?state=ALL")
                        .content(objectMapper.writeValueAsString(bookingDtoOut))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}