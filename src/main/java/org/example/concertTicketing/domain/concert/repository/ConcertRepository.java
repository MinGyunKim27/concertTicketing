package org.example.concertTicketing.domain.concert.repository;

import org.example.concertTicketing.domain.concert.entity.Concert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ConcertRepository extends JpaRepository<Concert,Long>, JpaSpecificationExecutor<Concert> {
    @EntityGraph(attributePaths = {"venue"})
    Page<Concert> findAll(Specification<Concert> spec, Pageable pageable);

}
