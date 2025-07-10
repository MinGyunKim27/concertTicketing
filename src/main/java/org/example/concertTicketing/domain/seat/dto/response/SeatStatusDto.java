package org.example.concertTicketing.domain.seat.dto.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SeatStatusDto {
    private Long seatId;
    private String rowLabel;
    private Integer columnNumber;
    private String seatLabel;
    private Long price;
    private Boolean isReserved;

    public SeatStatusDto(Long seatId, String rowLabel, Integer columnNumber, String seatLabel, Long price, Boolean isReserved) {
        this.seatId = seatId;
        this.rowLabel = rowLabel;
        this.columnNumber = columnNumber;
        this.seatLabel = seatLabel;
        this.price = price;
        this.isReserved = isReserved;
    }
}

