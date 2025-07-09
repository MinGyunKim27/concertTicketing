package org.example.concertTicketing.domain.venue.entity;
import jakarta.persistence.*;
import lombok.*;
import org.example.concertTicketing.domain.common.entity.Timestamped;


@Entity
@Table(name = "venues")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Venue extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 255)
    private String location;
}
