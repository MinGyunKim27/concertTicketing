package org.example.concertTicketing.domain.seat.repository;


import org.example.concertTicketing.domain.seat.dto.response.SeatStatusProjection;
import org.example.concertTicketing.domain.seat.entity.Seat;
import org.example.concertTicketing.domain.venue.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    Long countByVenue(Venue venue);

    @Query(value = """
    SELECT
        s.id AS seatId,
        s.row_label AS rowLabel,
        s.column_number AS columnNumber,
        s.label AS seatLabel,
        s.price AS price,
        CASE
            WHEN t.id IS NOT NULL THEN TRUE
            ELSE FALSE
        END AS isReserved
    FROM seats s
    LEFT JOIN tickets t
        ON s.id = t.seat_id
        AND t.concert_id = :concertId
    WHERE s.venue_id = (
        SELECT c.venue_id FROM concerts c WHERE c.id = :concertId
    )
    AND s.row_label = :rowLabel
""", nativeQuery = true)
    List<SeatStatusProjection> findSeatStatusesByConcertIdAndRowLabel(
            @Param("concertId") Long concertId,
            @Param("rowLabel") String rowLabel
    );

    List<Seat> findByLabelContaining(String a1);
}
