package org.example.concertTicketing.domain.concert.service;


import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.example.concertTicketing.domain.concert.dto.request.ConcertRequestDto;
import org.example.concertTicketing.domain.concert.dto.response.ConcertResponseDto;
import org.example.concertTicketing.domain.concert.entity.Concert;
import org.example.concertTicketing.domain.concert.repository.ConcertRepository;
import org.example.concertTicketing.domain.seat.dto.response.SeatResponseDto;
import org.example.concertTicketing.domain.seat.entity.Seat;
import org.example.concertTicketing.domain.seat.repository.SeatRepository;
import org.example.concertTicketing.domain.venue.entity.Venue;
import org.example.concertTicketing.domain.venue.repository.VenueRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConcertService {

    private final ConcertRepository concertRepository;
    private final VenueRepository venueRepository;
    private final SeatRepository seatRepository;

    private ConcertResponseDto buildResponseDto(Concert concert) {
        int total = seatRepository.countByConcert(concert);
        int sold  = seatRepository.countSoldByConcert(concert);
        return buildResponseDto(concert, total - sold);
    }

    private ConcertResponseDto buildResponseDto(Concert concert, int remainingTickets) {
        return ConcertResponseDto.builder()
                .id(concert.getId())
                .concertName(concert.getTitle())
                .date(concert.getDate())
                .venue(concert.getVenue().getName())
                .remainingTickets(remainingTickets)
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
    @Transactional
    public void deleteConcert(Long id) {
        if(!concertRepository.existsById(id)) {
            throw new IllegalArgumentException("해당 콘서트를 찾을 수 없습니다.");
        }
        concertRepository.deleteById(id);
    }

    // 콘서트 검색
    public Page<ConcertResponseDto> searchConcerts(
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
        return concertRepository.findAll(spec, pageable)
                .map(this::buildResponseDto);
    }

    // 콘서트 단건 조회
    public ConcertResponseDto getConcert(Long id) {
        Concert concert = concertRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 콘서트를 찾을 수 없습니다."));
                return buildResponseDto(concert, 0);
    }

    // 콘서트 좌석 조회
    public List<SeatResponseDto> getSeats(Long concertId, char rowLabel) {
        Concert concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new IllegalArgumentException("해당 콘서트를 찾을 수 없습니다."));

        List<Seat> seats = seatRepository.findbyConcertAndRowLabel(concert,rowLabel);

        return seats.stream()
                .map(seat -> SeatResponseDto.builder()
                        .seatId(seat.getId())
                        .label(seat.getLabel())
                        .rowLabel(seat.getRowLabel())
                        .column(seat.getColumn())
                        .isReserved(seat.isReserved())
                        .build()
                )
                .collect(Collectors.toList());
    }






}
