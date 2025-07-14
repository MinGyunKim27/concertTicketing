package org.example.concertTicketing.domain.concert.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

@Repository
@RequiredArgsConstructor
public class ConcertRepositoryQueryImpl implements ConcertRepositoryQuery{

    private final JPAQueryFactory queryFactory;


}
