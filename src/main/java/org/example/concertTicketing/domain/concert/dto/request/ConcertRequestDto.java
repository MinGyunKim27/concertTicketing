package org.example.concertTicketing.domain.concert.dto.request;


import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConcertRequestDto {
    private String concertName;
    private LocalDateTime date;
    private String venue;
}
