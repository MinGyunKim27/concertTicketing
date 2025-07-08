package org.example.concertTicketing.domain.seat.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.concertTicketing.domain.common.entity.Timestamped;
import org.example.concertTicketing.domain.venue.entity.Venue;

@Entity
@Table(name = "seats")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Seat extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    @Column(name = "row_label", length = 2, nullable = false)
    private String rowLabel;

    @Column(name = "column_number", nullable = false)
    private Integer column;

    @Column(length = 10, nullable = false)
    private String label;

    @Column(nullable = true)
    private Long price;

}
