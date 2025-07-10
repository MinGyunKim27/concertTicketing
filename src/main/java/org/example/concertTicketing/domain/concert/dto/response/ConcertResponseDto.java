package org.example.concertTicketing.domain.concert.dto.response;


import lombok.*;
import org.example.concertTicketing.domain.concert.entity.Concert;


import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConcertResponseDto {
    private Long id;
    private String concertName;
    private LocalDateTime date;
    private String venue;
    private Long remainingTickets;

    public static ConcertResponseDto buildResponseDto(Concert concert, Long remainingTickets) {
        return ConcertResponseDto.builder()
                .id(concert.getId())
                .concertName(concert.getTitle())
                .date(concert.getDate())
                .venue(concert.getVenue().getName())
                .remainingTickets(remainingTickets)
                .build();
    }
}
