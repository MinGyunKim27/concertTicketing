package org.example.concertTicketing.domain.ticket.repository;

import org.example.concertTicketing.domain.ticket.entity.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    // 회원별 티켓 조회(페이징)
    @Query("""
    SELECT t
    FROM Ticket t
    WHERE t.order.user.id = :userId
    """)
    Page<Ticket> findByUserId(@Param("userId") Long userId, Pageable pageable);

    // 주문 번호로 티켓 전체 조회 (예매 취소)
    List<Ticket> findAllByOrderId(Long orderId);

    // 이미 예약된 좌석 확인
    @Query("""
    SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END
        FROM Ticket t
        WHERE t.seat.id = :seatId
            AND t.order.concert.id = :concertId\s
            AND t.canceledAt IS NULL
    """)
    boolean existsBySeatIdAndConcertIdAndCanceledAtIsNull(
            @Param("seatId") Long seatId,
            @Param("concertId") Long concertId
    );

    // 좌석 리스트 중 에약된 좌석 ID 추출
    @Query("SELECT t.seat.id FROM Ticket t WHERE t.order.concert.id = :concertId AND t.seat.id IN :seatIds AND t.canceledAt IS NULL")
    List<Long> findReservedSeatIds(@Param("concertId") Long concertId, @Param("seatIds") List<Long> seatIds);
}
