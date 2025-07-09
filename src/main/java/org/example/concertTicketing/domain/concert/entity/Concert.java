package org.example.concertTicketing.domain.concert.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.concertTicketing.domain.common.entity.Timestamped;
import org.example.concertTicketing.domain.ticket.entity.Ticket;
import org.example.concertTicketing.domain.venue.entity.Venue;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "concerts") // 테이블명은 "users"
@Builder
public class Concert extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDateTime date;

    @OneToMany(mappedBy = "concert")
    private List<Ticket> tickets;

    public void update(String title, LocalDateTime date, Venue venue) {
        this.title = title;
        this.date  = date;
        this.venue = venue;
    }

}
