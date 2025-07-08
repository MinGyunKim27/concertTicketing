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

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "concert_id")
    private Concert concert;

    @ManyToOne
    @JoinColumn(name = "seat_id")
    private Seat seat;

    private Long orderId;

    private LocalDateTime reservedAt;

    private LocalDateTime canceledAt;


}
