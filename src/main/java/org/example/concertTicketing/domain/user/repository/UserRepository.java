package org.example.concertTicketing.domain.user.repository;

import org.example.concertTicketing.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
}
