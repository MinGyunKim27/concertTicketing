package org.example.concertTicketing.domain.seat.repository;

import org.example.concertTicketing.domain.seat.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatRepository extends JpaRepository<Seat, Long> {
}
