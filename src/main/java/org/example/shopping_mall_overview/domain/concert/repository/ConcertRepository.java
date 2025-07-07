package org.example.shopping_mall_overview.domain.concert.repository;

import org.example.shopping_mall_overview.domain.concert.entity.Concert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConcertRepository extends JpaRepository<Concert,Long> {
}
