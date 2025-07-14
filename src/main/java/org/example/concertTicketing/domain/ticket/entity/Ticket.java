package org.example.concertTicketing.domain.ticket.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.concertTicketing.domain.auth.dto.response.SignUpResponseDto;
import org.example.concertTicketing.domain.concert.entity.Concert;
import org.example.concertTicketing.domain.order.entity.Order;
import org.example.concertTicketing.domain.seat.entity.Seat;
import org.example.concertTicketing.domain.user.entity.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
@Getter
@Setter
@NoArgsConstructor
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id")
    private Seat seat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private LocalDateTime reservedAt;

    private LocalDateTime canceledAt;

    public static Ticket reserve(Order order, Seat seat, LocalDateTime now) {
        Ticket ticket = new Ticket();
        ticket.setOrder(order);
        ticket.setSeat(seat);
        ticket.setConcert(order.getConcert());
        ticket.setUser(order.getUser());
        ticket.setReservedAt(now);
        return ticket;
    }

    private void setUser(User user) {
    }

    private void setConcert(Concert concert) {
    }

    public void cancel() {
        this.canceledAt = LocalDateTime.now();
    }

    public boolean isCanceled() {
        return this.canceledAt != null;
    }

}
