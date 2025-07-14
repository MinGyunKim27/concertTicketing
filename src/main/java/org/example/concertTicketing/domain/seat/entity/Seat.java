package org.example.concertTicketing.domain.seat.entity;


import jakarta.persistence.*;
import lombok.*;
import org.example.concertTicketing.domain.common.entity.Timestamped;
import org.example.concertTicketing.domain.venue.entity.Venue;


@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Table(name = "seats", indexes = {
        @Index(name = "idx_seat_venue_id", columnList = "venue_id"),
        @Index(name = "idx_seat_row_label_column_number", columnList = "row_label, column_number")
})
public class Seat extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    @Column(name = "row_label", nullable = false)
    private String rowLabel;

    @Column(name = "column_number", nullable = false)
    private Integer column;

    @Column(length = 10, nullable = false)
    private String label;

    private Long price;
}
