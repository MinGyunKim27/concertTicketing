package org.example.concertTicketing.domain.concert.dto.response;


import lombok.*;
import org.example.concertTicketing.domain.concert.entity.Concert;


import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// implements Serializable 추가 하여 직렬화 진행 (캐시작업할때 dto의 데이터를 바이트로 바꾸는것)
public class ConcertResponseDto implements Serializable {
    private Long id;
    private String concertName;
    private LocalDateTime date;
    private String venue;
    private Long remainingTickets;
    private int viewCount;
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
