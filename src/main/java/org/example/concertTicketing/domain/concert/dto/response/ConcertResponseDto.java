package org.example.concertTicketing.domain.concert.dto.response;


import lombok.*;


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
    private Integer remainingTickets;
}
