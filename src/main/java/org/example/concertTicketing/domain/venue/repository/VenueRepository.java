package org.example.concertTicketing.domain.venue.repository;

import org.example.concertTicketing.domain.venue.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VenueRepository extends JpaRepository<Venue, Long> {
    boolean existsByName(String name);


    Optional<Venue> findByName(String name);
}
