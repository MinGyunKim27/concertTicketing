package org.example.concertTicketing.domain.concert.repository;

import org.example.concertTicketing.domain.concert.entity.Concert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ConcertRepository extends JpaRepository<Concert,Long>, JpaSpecificationExecutor<Concert> {

}
