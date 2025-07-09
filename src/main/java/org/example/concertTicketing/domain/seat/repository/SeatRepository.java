package org.example.concertTicketing.domain.seat.repository;

import org.example.concertTicketing.domain.concert.entity.Concert;
import org.example.concertTicketing.domain.seat.entity.Seat;
import org.example.concertTicketing.domain.venue.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByVenueAndRowLabel(Venue venue, char rowLabel);

    int countByVenue(Venue venue);

    int countSoldByVenue(Venue venue);
}
