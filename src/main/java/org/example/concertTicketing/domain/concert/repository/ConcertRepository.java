package org.example.concertTicketing.domain.concert.repository;

import org.example.concertTicketing.domain.concert.entity.Concert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConcertRepository extends JpaRepository<Concert,Long> {
}
