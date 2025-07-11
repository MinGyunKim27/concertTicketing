package org.example.concertTicketing.domain.concert.dto.response;


import lombok.*;


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
    private Integer remainingTickets;
    private int viewCount;
}
