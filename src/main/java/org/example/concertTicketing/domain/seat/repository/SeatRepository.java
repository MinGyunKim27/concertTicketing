package org.example.concertTicketing.domain.seat.repository;

import org.example.concertTicketing.domain.concert.entity.Concert;
import org.example.concertTicketing.domain.seat.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findbyConcertAndRowLabel(Concert concert, char rowLabel);

    int countByConcert(Concert concert);

    int countSoldByConcert(Concert concert);
}
