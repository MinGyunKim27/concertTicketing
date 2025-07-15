package org.example.concertTicketing;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.example.concertTicketing.domain.seat.entity.Seat;
import org.example.concertTicketing.domain.seat.repository.SeatRepository;
import org.example.concertTicketing.domain.venue.entity.Venue;
import org.example.concertTicketing.domain.venue.repository.VenueRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
@RequiredArgsConstructor
public class DataInitializer {

    private final SeatRepository seatRepository;
    private final VenueRepository venueRepository;

    @PostConstruct
    public void init() {
        createVenueWithSeats("예술의전당", "서울 서초구");
        createVenueWithSeats("고양 종합 운동장", "고양시 일산동구");
        createVenueWithSeats("고척돔", "서울 구로구");
    }

    private void createVenueWithSeats(String name, String location) {
        if (venueRepository.existsByName(name)) return;

        Venue venue = venueRepository.save(
                Venue.builder()
                        .name(name)
                        .location(location)
                        .build()
        );

        for (char row = 'A'; row <= 'T'; row++) {
            long price = 50000L + ((row - 'A') * 10000L); // A=0, B=1, C=2...

            for (int col = 1; col <= 30; col++) {
                String seatLabel = row + String.valueOf(col);
                Seat seat = Seat.builder()
                        .venue(venue)
                        .rowLabel(String.valueOf(row))
                        .column(col)
                        .label(seatLabel)
                        .price(price)
                        .build();
                seatRepository.save(seat);
            }
        }

    }
}
