package org.example.concertTicketing.domain.venue.repository;

import org.example.concertTicketing.domain.venue.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VenueRepository extends JpaRepository<Venue, Long> {
    boolean existsByName(String name);
}
