package org.example.concertTicketing.domain.concert.repository;

import org.example.concertTicketing.domain.concert.dto.response.ConcertWithRemainingTicketsProjection;
import org.example.concertTicketing.domain.concert.entity.Concert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ConcertRepository extends JpaRepository<Concert,Long>, JpaSpecificationExecutor<Concert> {
    @EntityGraph(attributePaths = {"venue"})
    Page<Concert> findAll(Specification<Concert> spec, Pageable pageable);

    @Query(
            value = """
    SELECT
        COUNT(s.id) - COUNT(t.id) AS remainingTickets
    FROM concerts c
    JOIN venues v ON c.venue_id = v.id
    LEFT JOIN seats s ON s.venue_id = v.id
    LEFT JOIN tickets t ON t.seat_id = s.id AND t.canceled_at IS NULL
    WHERE c.id = :concertId
    GROUP BY c.id
    """,
            nativeQuery = true
    )
    Long countRemainingSeatsByConcertId(@Param("concertId") Long concertId);



    @Query(value = """
    SELECT
        c.id AS id,
        c.title AS concertName,
        c.date AS date,
        v.name AS venue,
        (COUNT(s.id) - COUNT(t.id)) AS remainingTickets
    FROM concerts c
    JOIN venues v ON c.venue_id = v.id
    LEFT JOIN seats s ON s.venue_id = v.id
    LEFT JOIN tickets t ON t.seat_id = s.id AND t.canceled_at IS NULL
    WHERE (:searchText IS NULL 
           OR c.title LIKE CONCAT('%', :searchText, '%')
           OR v.name LIKE CONCAT('%', :searchText, '%'))
      AND (:searchStartDate IS NULL OR c.date >= :searchStartDate)
      AND (:searchEndDate IS NULL OR c.date <= :searchEndDate)
    GROUP BY c.id, c.title, c.date, v.name
    ORDER BY c.date DESC
    LIMIT :limit OFFSET :offset
""", nativeQuery = true)
    List<ConcertWithRemainingTicketsProjection> searchConcertsWithRemainingTicketsNative(
            @Param("searchText") String searchText,
            @Param("searchStartDate") LocalDateTime searchStartDate,
            @Param("searchEndDate") LocalDateTime searchEndDate,
            @Param("limit") int limit,
            @Param("offset") int offset
    );


    @Query(value = """
    SELECT COUNT(*)
    FROM concerts c
    JOIN venues v ON c.venue_id = v.id
    WHERE (:searchText IS NULL 
           OR c.title LIKE CONCAT('%', :searchText, '%')
           OR v.name LIKE CONCAT('%', :searchText, '%'))
      AND (:searchStartDate IS NULL OR c.date >= :searchStartDate)
      AND (:searchEndDate IS NULL OR c.date <= :searchEndDate)
    """, nativeQuery = true)
    Long countConcertsNative(
            @Param("searchText") String searchText,
            @Param("searchStartDate") LocalDateTime searchStartDate,
            @Param("searchEndDate") LocalDateTime searchEndDate
    );

}
