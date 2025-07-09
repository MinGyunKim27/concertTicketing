package org.example.concertTicketing.domain.user.repository;

import org.example.concertTicketing.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    boolean existsByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByIdAndIsDeletedFalse(Long userId);

    List<User> findByUsernameContaining(String username);
}
