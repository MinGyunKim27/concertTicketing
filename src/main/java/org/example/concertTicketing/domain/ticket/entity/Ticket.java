package org.example.concertTicketing.domain.ticket.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.concertTicketing.domain.concert.entity.Concert;
import org.example.concertTicketing.domain.seat.entity.Seat;
import org.example.concertTicketing.domain.user.entity.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
@Getter
@NoArgsConstructor
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id")
    private Concert concert;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id")
    private Seat seat;

    private Long orderId;

    private LocalDateTime reservedAt;

    private LocalDateTime canceledAt;

    public static Ticket reserve(User user, Concert concert, Seat seat, Long orderId) {
        Ticket ticket = new Ticket();
        ticket.user = user;
        ticket.concert = concert;
        ticket.seat = seat;
        ticket.orderId = orderId;
        ticket.reservedAt = LocalDateTime.now();
        return ticket;
    }

    public void cancel() {
        this.canceledAt = LocalDateTime.now();
    }

    public boolean isCanceled() {
        return this.canceledAt != null;
    }
}
