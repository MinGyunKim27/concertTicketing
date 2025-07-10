package org.example.concertTicketing.domain.concert.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.concertTicketing.domain.common.dto.CommonResponseDto;
import org.example.concertTicketing.domain.common.dto.PagedResponse;
import org.example.concertTicketing.domain.concert.dto.request.ConcertRequestDto;
import org.example.concertTicketing.domain.concert.dto.response.ConcertResponseDto;
import org.example.concertTicketing.domain.concert.service.ConcertService;
import org.example.concertTicketing.domain.seat.dto.response.SeatResponseDto;
import org.example.concertTicketing.domain.seat.dto.response.SeatStatusDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ConcertController {

    private final ConcertService concertService;

    // 콘서트 생성
    // 굳
    @PostMapping("/api/admin/concerts")
    public CommonResponseDto<ConcertResponseDto> createConcert(
            @RequestBody @Valid ConcertRequestDto dto
            ) {
        ConcertResponseDto data = concertService.createConcert(dto);
        return CommonResponseDto.ok("콘서트 생성에 성공했습니다.",data);
    }

    // 콘서트 수정
    // 굳
    @PatchMapping("/api/admin/concerts/{id}")
    public CommonResponseDto<ConcertResponseDto> updateConcert(
            @PathVariable Long id,
            @RequestBody @Valid ConcertRequestDto dto
    ) {
        ConcertResponseDto data = concertService.updateConcert(id, dto);
        return CommonResponseDto.ok("콘서트 수정에 성공했습니다.",data);
    }

    // 콘서트 삭제
    // 굳
    @DeleteMapping("/api/admin/concerts/{id}")
    public CommonResponseDto<Void> deleteConcert(@PathVariable Long id) {
        concertService.deleteConcert(id);
        return CommonResponseDto.ok("콘서트 삭제에 성공했습니다.", null);
    }

    // 콘서트 검색
    @GetMapping("/api/concerts")
    public CommonResponseDto<PagedResponse<ConcertResponseDto>> searchConcerts(
            @RequestParam(required = false) String searchText,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime searchStartDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime searchEndDate,
            Pageable pageable
    ) {
        PagedResponse<ConcertResponseDto> page = concertService.searchConcerts(
                searchText, searchStartDate, searchEndDate, pageable);
        return CommonResponseDto.ok("콘서트 검색에 성공했습니다.", page);
    }

    // 콘서트 단건 조회
    @GetMapping("/api/concerts/{id}")
    public CommonResponseDto<ConcertResponseDto> getConcert(@PathVariable Long id) {
        ConcertResponseDto data = concertService.getConcert(id);
        return CommonResponseDto.ok("콘서트 조회에 성공했습니다.", data);
    }

    // 콘서트 좌석 조회
    @GetMapping("/api/concerts/{concertId}/seats/{rowLabel}")
    public CommonResponseDto<List<SeatStatusDto>> getSeats(
            @PathVariable Long concertId,
            @PathVariable String rowLabel
    ) {
        List<SeatStatusDto> seats = concertService.getSeats(concertId, rowLabel);
        return CommonResponseDto.ok("콘서트 좌석 조회에 성공했습니다.", seats);
    }
}
