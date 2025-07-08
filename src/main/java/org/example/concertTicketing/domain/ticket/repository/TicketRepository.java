package org.example.concertTicketing.domain.ticket.repository;

import org.example.concertTicketing.domain.ticket.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
}
