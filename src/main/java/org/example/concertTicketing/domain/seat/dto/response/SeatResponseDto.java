package org.example.concertTicketing.domain.seat.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatResponseDto {
    private Long seatId;
    private String label;
    private String rowLabel;
    private Integer column;
    private Boolean isReserved;
}