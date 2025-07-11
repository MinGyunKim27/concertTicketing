package org.example.concertTicketing.domain.concert.service;


import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConcertService {

    private final ConcertRepository concertRepository;
    private final VenueRepository venueRepository;
    private final SeatRepository seatRepository;


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
                        p.getIsReserved()
                ))
                .collect(Collectors.toList());
    }
}
