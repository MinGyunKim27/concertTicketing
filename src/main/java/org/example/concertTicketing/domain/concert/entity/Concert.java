package org.example.concertTicketing.domain.concert.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.concertTicketing.domain.common.entity.Timestamped;

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
}
