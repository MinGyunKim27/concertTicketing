package org.example.concertTicketing.domain.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.concertTicketing.domain.concert.entity.Concert;
import org.example.concertTicketing.domain.user.entity.User;

import java.time.LocalDateTime;

@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id")
    private Concert concert;

    private LocalDateTime createdAt;

    public static Order create(User user, Concert concert, LocalDateTime createdAt) {
        Order order = new Order();
        order.setUser(user);
        order.setConcert(concert);
        order.setCreatedAt(createdAt);
        return order;
    }

}
