//package org.example.concertTicketing;
//
//import org.example.concertTicketing.domain.concert.entity.Concert;
//import org.example.concertTicketing.domain.concert.repository.ConcertRepository;
//import org.example.concertTicketing.domain.seat.entity.Seat;
//import org.example.concertTicketing.domain.seat.repository.SeatRepository;
//import org.example.concertTicketing.domain.ticket.dto.request.TicketReserveRequestDto;
//import org.example.concertTicketing.domain.ticket.repository.TicketRepository;
//import org.example.concertTicketing.domain.ticket.service.TicketService;
//import org.example.concertTicketing.domain.user.UserRole;
//import org.example.concertTicketing.domain.user.repository.UserRepository;
//import org.example.concertTicketing.domain.venue.entity.Venue;
//import org.example.concertTicketing.domain.venue.repository.VenueRepository;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.example.concertTicketing.domain.user.entity.User;
//import org.springframework.context.ApplicationContext;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.UUID;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//@ActiveProfiles("test")
//@SpringBootTest
//@DisplayName("동시성 테스트 - 좌석 중복 예약 방지")
//class SeatConcurrencyTest {
//
//    @Autowired
//    private TicketService ticketService;
//
//    @Autowired
//    private TicketRepository ticketRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private ConcertRepository concertRepository;
//
//    @Autowired
//    private VenueRepository venueRepository;
//
//    @Autowired
//    private SeatRepository seatRepository;
//
//    @Autowired
//    private ApplicationContext context;
//
//    private Long concertId;
//    private Long seatId;
//    private Long seatId1;
//    private Long seatId2;
//
//    private static final int THREAD_COUNT = 10;
//
//    @BeforeEach
//    void setup() {
//
//        String uuid = UUID.randomUUID().toString().substring(0, 8); // 8자리만 잘라서
//        String username = "user_" + uuid;
//        String email = username + "@test.com";
//
//        User user = userRepository.save(User.builder()
//                .username(username)
//                .nickname("닉네임")
//                .email(email)
//                .password("encodedPw")
//                .userRole(UserRole.USER)
//                .build());
//
//        Venue venue = venueRepository.save(
//                Venue.builder()
//                        .name("서울 예술의 전당")
//                        .location("서울 서초구")
//                        .build()
//        );
//        Concert concert = concertRepository.save(Concert.builder()
//                .title("테스트 콘서트")
//                .date(LocalDateTime.now().plusDays(7))
//                .venue(venue)
//                .build());
//        Seat seat = seatRepository.save(Seat.builder()
//                .venue(venue)
//                .rowLabel("A")
//                .column(1)
//                .label("A1")
//                .price(10000L)
//                .build());
//
//        Seat seat1 = seatRepository.save(Seat.builder()
//                .venue(venue)
//                .rowLabel("A")
//                .column(2)
//                .label("A2")
//                .price(10000L)
//                .build());
//
//        Seat seat2 = seatRepository.save(Seat.builder()
//                .venue(venue)
//                .rowLabel("A")
//                .column(3)
//                .label("A3")
//                .price(10000L)
//                .build());
//
//        concertId = concert.getId();
//        seatId = seat.getId();
//        seatId1 = seat1.getId();
//        seatId2 = seat2.getId();
//    }
//
//    @Test
//    void 동시에_같은_좌석을_예약할때_중복되지_않아야한다_v1() throws InterruptedException {
//        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
//        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
//
//
//        for (int i = 0; i < THREAD_COUNT; i++) {
//            String randomSuffix = UUID.randomUUID().toString().substring(0, 8);
//            final Long userId = userRepository.save(
//                    User.builder()
//                            .username("user" + randomSuffix)
//                            .nickname("닉네임" + i)
//                            .email("user" + randomSuffix + "@test.com")
//                            .password("encodedPw")
//                            .userRole(UserRole.USER)
//                            .build()
//            ).getId();
//            executorService.execute(() -> {
//                try {
//                    TicketReserveRequestDto dto = new TicketReserveRequestDto(List.of(seatId));
//                    ticketService.reserveTicketsLettuce(userId, concertId, dto);
//                } catch (Exception e) {
//                    System.out.println("예약 실패 → " + e.getMessage());
//                } finally {
//                    latch.countDown();
//                }
//            });
//        }
//
//        latch.await();
//
//        long reservedCount = ticketRepository.countBySeatId(seatId);
//        Assertions.assertEquals(1, reservedCount); // 해당 좌석은 단 1명만 예약 가능해야 함
//    }
//
//    @Test
//    void 동시에_같은_좌석을_예약할때_중복되지_않아야한다_v2() throws InterruptedException {
//        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
//        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
//
//
//        for (int i = 0; i < THREAD_COUNT; i++) {
//            String randomSuffix = UUID.randomUUID().toString().substring(0, 8);
//            final Long userId = userRepository.save(
//                    User.builder()
//                            .username("user" + randomSuffix)
//                            .nickname("닉네임" + i)
//                            .email("user" + randomSuffix + "@test.com")
//                            .password("encodedPw")
//                            .userRole(UserRole.USER)
//                            .build()
//            ).getId();
//            executorService.execute(() -> {
//                try {
//                    TicketReserveRequestDto dto = new TicketReserveRequestDto(List.of(seatId1));
//                    ticketService.reserveTicketsRedisson(userId, concertId, dto);
//                } catch (Exception e) {
//                    System.out.println("예약 실패 → " + e.getMessage());
//                } finally {
//                    latch.countDown();
//                }
//            });
//        }
//
//        latch.await();
//
//        long reservedCount = ticketRepository.countBySeatId(seatId1);
//        Assertions.assertEquals(1, reservedCount); // 해당 좌석은 단 1명만 예약 가능해야 함
//    }
//
//
//    @Test
//    void 동시에_같은_좌석을_예약할때_중복되지_않아야한다_v3() throws InterruptedException {
//        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
//        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
//        TicketService proxy = context.getBean(TicketService.class);
//
//        for (int i = 0; i < THREAD_COUNT; i++) {
//            String randomSuffix = UUID.randomUUID().toString().substring(0, 8);
//            final Long userId = userRepository.save(
//                    User.builder()
//                            .username("user" + randomSuffix)
//                            .nickname("닉네임" + i)
//                            .email("user" + randomSuffix + "@test.com")
//                            .password("encodedPw")
//                            .userRole(UserRole.USER)
//                            .build()
//            ).getId();
//            executorService.execute(() -> {
//                try {
//                    TicketReserveRequestDto dto = new TicketReserveRequestDto(List.of(seatId2));
//                    proxy.reserveTicketsAop(userId, concertId, dto);
//                } catch (Exception e) {
//                    System.out.println("예약 실패 → " + e.getMessage());
//                } finally {
//                    latch.countDown();
//                }
//            });
//        }
//
//        latch.await();
//
//        long reservedCount = ticketRepository.countBySeatId(seatId2);
//        Assertions.assertEquals(1, reservedCount); // 해당 좌석은 단 1명만 예약 가능해야 함
//    }
//}