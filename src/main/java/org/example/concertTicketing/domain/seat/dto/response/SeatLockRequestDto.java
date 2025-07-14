package org.example.concertTicketing.domain.seat.dto.response;

import java.util.List;

public record SeatLockRequestDto(List<Long> seatIds) {
}
