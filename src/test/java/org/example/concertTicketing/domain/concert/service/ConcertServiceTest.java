package org.example.concertTicketing.domain.concert.service;

import org.example.concertTicketing.domain.concert.dto.request.ConcertRequestDto;
import org.example.concertTicketing.domain.concert.dto.response.ConcertResponseDto;
import org.example.concertTicketing.domain.venue.entity.Venue;
import org.example.concertTicketing.domain.venue.repository.VenueRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ConcertServiceTest {

    @Autowired
    private ConcertService concertService;

    @Autowired
    private VenueRepository venueRepository;

    private static Venue savedVenue;


    @BeforeAll
    void setUp() {
        // 테스트마다 venue 생성 (name 중복 체크 필요)
        savedVenue = venueRepository.save(
                Venue.builder()
                        .name("테스트 공연장")
                        .location("테스트 지역")
                        .build()
        );
    }


    @Test
    @DisplayName("콘서트를 생성하면 저장된 DTO가 반환되어야 한다")
    void createConcert_success() {
        // given
        ConcertRequestDto dto = ConcertRequestDto.builder()
                .concertName("클래식 공연")
                .date(LocalDateTime.now().plusDays(10))
                .venue(savedVenue.getName())
                .build();

        // when
        ConcertResponseDto result = concertService.createConcert(dto);

        // then
        assertThat(result.getConcertName()).isEqualTo("클래식 공연");
        assertThat(result.getVenue()).isEqualTo(savedVenue.getName());
        assertThat(result.getRemainingTickets()).isEqualTo(0L);
    }

    @Test
    @DisplayName("존재하지 않는 콘서트 수정 시 예외가 발생한다")
    void updateConcert_fail_not_found() {

        // given
        ConcertRequestDto dto = ConcertRequestDto.builder()
                .concertName("업데이트 공연")
                .date(LocalDateTime.now().plusDays(5))
                .venue(savedVenue.getName())
                .build();

        // when & then
        assertThatThrownBy(() -> concertService.updateConcert(9999L, dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("해당 콘서트를 찾을 수 없습니다");
    }

    @Test
    @DisplayName("콘서트를 정상적으로 삭제할 수 있다")
    void deleteConcert_success() {

        // given
        ConcertRequestDto dto = ConcertRequestDto.builder()
                .concertName("삭제할 공연")
                .date(LocalDateTime.now().plusDays(3))
                .venue(savedVenue.getName())
                .build();

        ConcertResponseDto saved = concertService.createConcert(dto);

        // when
        concertService.deleteConcert(saved.getId());

        // then
        assertThatThrownBy(() -> concertService.getConcert(saved.getId()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // searchConcerts, getSeats 등은 seat/예약 DB 설정 후 통합 테스트로 따로 구성 가능
}
