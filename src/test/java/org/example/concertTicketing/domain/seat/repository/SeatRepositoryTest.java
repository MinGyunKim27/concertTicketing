package org.example.concertTicketing.domain.seat.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.example.concertTicketing.domain.seat.entity.Seat;
import org.example.concertTicketing.domain.venue.entity.Venue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Transactional
public class SeatRepositoryTest {

    @Autowired
    private SeatRepository seatRepository;

    @PersistenceContext
    private EntityManager em;

    private static final int TOTAL = 1_000_000;
    private static final int BATCH_SIZE = 10_000;

    @BeforeEach
    void insertTestData() {
        if (seatRepository.count() > 0) return;

        Venue venue = new Venue(1L, "고척돔", "서울 구로구");


        List<Seat> seats = new ArrayList<>(BATCH_SIZE);

        for (int i = 0; i < TOTAL; i++) {
            seats.add(Seat.builder()
                    .venue(venue)
                    .rowLabel("A")
                    .column(i)
                    .label("A" + i)
                    .price(50000L)
                    .build());


            if (seats.size() == BATCH_SIZE) {
                for (Seat seat : seats) {
                    em.persist(seat);
                }
                em.flush();
                em.clear();
                seats.clear();
            }
        }
        // 남은 데이터 마저 저장
        if (!seats.isEmpty()) {
            for (Seat seat : seats) {
                em.persist(seat);
            }
            em.flush();
            em.clear();
        }
    }

    @Test
    void testSearchPerformanceBeforeAndAfterIndex() {
        long start = System.currentTimeMillis();
        List<Seat> result = seatRepository.findByLabelContaining("A1");
        long end = System.currentTimeMillis();

        System.out.println("검색 소요 시간(ms): " + (end - start));
        System.out.println("검색 결과 개수: " + result.size());
    }
}
