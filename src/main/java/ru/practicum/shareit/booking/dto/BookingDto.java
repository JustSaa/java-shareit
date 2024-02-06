package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class BookingDto {
    private Integer id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Integer bookerId;
}
