package org.example.concertTicketing.domain.concert.service;


import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.concertTicketing.domain.common.dto.PagedResponse;
import org.example.concertTicketing.domain.concert.dto.request.ConcertRequestDto;
import org.example.concertTicketing.domain.concert.dto.response.ConcertResponseDto;
import org.example.concertTicketing.domain.concert.entity.Concert;
import org.example.concertTicketing.domain.concert.repository.ConcertRepository;
import org.example.concertTicketing.domain.seat.dto.response.SeatResponseDto;
import org.example.concertTicketing.domain.seat.dto.response.SeatStatusDto;
import org.example.concertTicketing.domain.seat.dto.response.SeatStatusProjection;
import org.example.concertTicketing.domain.seat.entity.Seat;
import org.example.concertTicketing.domain.seat.repository.SeatRepository;
import org.example.concertTicketing.domain.venue.entity.Venue;
import org.example.concertTicketing.domain.venue.repository.VenueRepository;
import org.slf4j.Logger;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.querydsl.core.types.dsl.Wildcard.count;
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConcertService {

    private final ConcertRepository concertRepository;
    private final VenueRepository venueRepository;
    private final SeatRepository seatRepository;
    //Redis 에서 사용할 키의 이름
    private static final String VIEW_COUNT_KEY = "concert:viewcount";
    //Redis와 상호작용하는 객체이다.>>opsForHash()를 통해 Hash명령어를 쉽게 사용가능하게함.
    private final RedisTemplate<String, Object> redisTemplate;

    //이걸로 항상 카운트 한걸 넣어주면 항상 쿼리가 3번 이상 실행됩니다 아예 로직을 바꿔야함
    //조회수 추가
    private ConcertResponseDto buildResponseDto(Concert concert) {
        Venue venue = concert.getVenue();
        int total = seatRepository.countByVenue(venue);
        int sold2 = 0;
        int viewCount = getViewCount(concert.getId());
        return buildResponseDto(concert, total - sold2, viewCount);
    }

    private ConcertResponseDto buildResponseDto(Concert concert, int remainingTickets, int viewCount) {
        return ConcertResponseDto.builder()
                .id(concert.getId())
                .concertName(concert.getTitle())
                .date(concert.getDate())
                .venue(concert.getVenue().getName())
                .remainingTickets(remainingTickets)
                .viewCount(viewCount)
                .build();
    }

    // 콘서트 생성
    @Transactional
    public ConcertResponseDto createConcert(ConcertRequestDto dto) {

        Venue venue = venueRepository.findByName(dto.getVenue())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공연장입니다."));

        Concert concert = Concert.builder()
                .title(dto.getConcertName())
                .date(dto.getDate())
                .venue(venue)
                .build();

        concertRepository.save(concert);

        return buildResponseDto(concert);
    }

    // 콘서트 수정
    @CacheEvict(value = "concert", key = "#id") // << 캐시 사용시 수정될때(캐시 무효화) 어노테이션입니다.
    @Transactional
    public ConcertResponseDto updateConcert(Long id, ConcertRequestDto dto) {
        Concert concert = concertRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 콘서트를 찾을 수 없습니다."));

        Venue venue = venueRepository.findByName(dto.getVenue())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공연장입니다."));

        concert.update(dto.getConcertName(), dto.getDate(), venue);

        return buildResponseDto(concert);
    }

    // 콘서트 삭제
    @CacheEvict(value = "concert", key = "#id") //  << 캐시 사용시 삭제될때 어노테이션입니다.
    @Transactional
    public void deleteConcert(Long id) {
        if(!concertRepository.existsById(id)) {
            throw new IllegalArgumentException("해당 콘서트를 찾을 수 없습니다.");
        }
        concertRepository.deleteById(id);
    }

    // 콘서트 검색
    public PagedResponse<ConcertResponseDto> searchConcerts(
            String searchText,
            LocalDateTime searchStartDate,
            LocalDateTime searchEndDate,
            Pageable pageable
    ) {
        Specification<Concert> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (searchText != null && !searchText.isBlank()) {
                String like = "%" + searchText.trim() + "%";
                predicates.add(cb.or(
                        cb.like(root.get("title"), like),
                        cb.like(root.join("venue").get("name"), like)
                ));
            }
            if (searchStartDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("date"), searchStartDate));
            }
            if (searchEndDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("date"), searchEndDate));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return PagedResponse.from(concertRepository.findAll(spec, pageable)
                .map(this::buildResponseDto));
    }

    // 콘서트 단건 조회
    public ConcertResponseDto getConcert(Long id, String userIdOrIp) {
        String concertKey = "concert:" + id;
        // 1. Redis에서 캐시된 콘서트 DTO 가져오기
        ConcertResponseDto dto = (ConcertResponseDto) redisTemplate.opsForValue().get(concertKey);

        if (dto == null) {
            // 2. 캐시에 없으면 DB에서 조회
            Concert concert = concertRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("해당 콘서트를 찾을 수 없습니다."));
            int remaining = seatRepository.countByVenue(concert.getVenue());

            dto = ConcertResponseDto.builder()
                    .id(concert.getId())
                    .concertName(concert.getTitle())
                    .date(concert.getDate())
                    .venue(concert.getVenue().getName())
                    .remainingTickets(remaining)
                    .viewCount(0) // 초기값
                    .build();

            // 캐시에 저장 (10분)
            redisTemplate.opsForValue().set(concertKey, dto, Duration.ofMinutes(10));
        }

        // 3. 조회수 증가
        incrementViewCount(id, userIdOrIp);
        // 4. 최신 조회수 가져옴
        int viewCount = getViewCount(id);

        // 4. 최신 조회수를 반영해서 DTO 새로 만들기
        return ConcertResponseDto.builder()
                .id(dto.getId())
                .concertName(dto.getConcertName())
                .date(dto.getDate())
                .venue(dto.getVenue())
                .remainingTickets(dto.getRemainingTickets())
                .viewCount(viewCount)
                .build();
    }
    // 콘서트 좌석 조회
    public List<SeatStatusDto> getSeats(Long concertId, String rowLabel) {
        List<SeatStatusProjection> projections = seatRepository.findSeatStatusesByConcertIdAndRowLabel(concertId, rowLabel);
        return projections.stream()
                .map(p -> new SeatStatusDto(
                        p.getSeatId(),
                        p.getRowLabel(),
                        p.getColumnNumber(),
                        p.getSeatLabel(),
                        p.getPrice(),
                        p.getIsReserved() != 0
                ))
                .collect(Collectors.toList());
    }
    //조회수를 읽어온다. (Redis)
    public int getViewCount(Long concertId) {
        Object count = redisTemplate.opsForHash().get(VIEW_COUNT_KEY, concertId.toString());
        if (count == null) return 0;
        return Integer.parseInt(count.toString());
    }
    public boolean incrementViewCount(Long concertId, String userIdOrIp) {
        String userKey = "concert:view:user:" + concertId;
        String viewCountKey = "concert:viewcount";
        String rankKey = "concert:rank";

        // 1. 유저가 이미 조회했는지 체크
        Boolean isNewView = redisTemplate.opsForSet().add(userKey, userIdOrIp) == 1;

        if (isNewView) {
            // 조회수 1 증가
            redisTemplate.opsForHash().increment(viewCountKey, concertId.toString(), 1);
            // 랭킹 점수 증가
            redisTemplate.opsForZSet().incrementScore(rankKey, concertId.toString(), 1);
            // Set TTL 자정까지
            redisTemplate.expire(userKey, getTTLUntilMidnight()); // 자정때 초기화
            return true;
        }
        return false; // 이미 조회함
    }

    // 현재 시간부터 다음 자정까지 남은 시간을 계산해서 TTL로 설정
    private Duration getTTLUntilMidnight() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime midnight = now.toLocalDate().plusDays(1).atStartOfDay();
        return Duration.between(now, midnight);
    }
}
