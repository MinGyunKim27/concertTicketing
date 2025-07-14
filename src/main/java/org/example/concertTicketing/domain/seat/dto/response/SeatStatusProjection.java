package org.example.concertTicketing.domain.seat.dto.response;

public interface SeatStatusProjection {
    Long getSeatId();
    String getRowLabel();
    Integer getColumnNumber();
    String getSeatLabel();
    Long getPrice();
    Boolean getIsReserved();
}
