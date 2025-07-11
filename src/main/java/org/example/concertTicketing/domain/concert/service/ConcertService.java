package org.example.concertTicketing.domain.concert.service;


import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.concertTicketing.domain.common.dto.PagedResponse;
import org.example.concertTicketing.domain.concert.dto.request.ConcertRequestDto;
import org.example.concertTicketing.domain.concert.dto.response.ConcertResponseDto;
import org.example.concertTicketing.domain.concert.dto.response.ConcertWithRemainingTicketsProjection;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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

    // 콘서트 생성
    // 굳
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
        return ConcertResponseDto.buildResponseDto(concert,seatRepository.countByVenue(venue));
    }

    // 콘서트 수정
    // 굳
    @CacheEvict(value = "concert", key = "#id") // << 캐시 사용시 수정될때(캐시 무효화) 어노테이션입니다.
    @Transactional
    public ConcertResponseDto updateConcert(Long id, ConcertRequestDto dto) {
        Concert concert = concertRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 콘서트를 찾을 수 없습니다."));

        Venue venue = venueRepository.findByName(dto.getVenue())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공연장입니다."));

        concert.update(dto.getConcertName(), dto.getDate(), venue);

        Long remainingSeat = concertRepository.countRemainingSeatsByConcertId(id);

        return ConcertResponseDto.buildResponseDto(concert,remainingSeat);
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
        int offset = (int) pageable.getOffset();
        int limit = pageable.getPageSize();

        // 1. Native Projection 쿼리 실행
        List<ConcertWithRemainingTicketsProjection> projections =
                concertRepository.searchConcertsWithRemainingTicketsNative(
                        searchText, searchStartDate, searchEndDate, limit, offset
                );

        // 2. concertId 목록 추출 후 Concert 엔티티 조회
        List<Long> concertIds = projections.stream()
                .map(ConcertWithRemainingTicketsProjection::getId)
                .toList();

        Map<Long, Concert> concertMap = concertRepository.findAllById(concertIds).stream()
                .collect(Collectors.toMap(Concert::getId, Function.identity()));

        // 3. DTO 생성
        List<ConcertResponseDto> dtoList = projections.stream()
                .map(p -> ConcertResponseDto.buildResponseDto(
                        concertMap.get(p.getId()),
                        p.getRemainingTickets()
                ))
                .toList();

        // 4. 전체 개수 조회
        long total = concertRepository.countConcertsNative(searchText, searchStartDate, searchEndDate);

        // 5. Page → PagedResponse
        Page<ConcertResponseDto> dtoPage = new PageImpl<>(dtoList, pageable, total);
        return PagedResponse.from(dtoPage);
    }

    // 콘서트 단건 조회
    public ConcertResponseDto getConcert(Long id) {
        Concert concert = concertRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 콘서트를 찾을 수 없습니다."));

        Long remainingSeat = concertRepository.countRemainingSeatsByConcertId(id);
        return ConcertResponseDto.buildResponseDto(concert,remainingSeat);
    }
    public ConcertResponseDto getConcert(Long id, String userIdOrIp) {
        String concertKey = "concert:" + id;
        // 1. Redis에서 캐시된 콘서트 DTO 가져오기
        ConcertResponseDto dto = (ConcertResponseDto) redisTemplate.opsForValue().get(concertKey);

        if (dto == null) {
            // 2. 캐시에 없으면 DB에서 조회
            Concert concert = concertRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("해당 콘서트를 찾을 수 없습니다."));
            long remaining = seatRepository.countByVenue(concert.getVenue());

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
