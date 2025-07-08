package org.example.concertTicketing.domain.user.repository;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.example.concertTicketing.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Page<User> findByUsernameContaining(String username, Pageable pageable);

    Optional<User> findByEmail(@NotBlank @Email String email);

    boolean existsByEmail(@NotBlank @Email String email);

    Optional<User> findByUsername(@NotBlank String username);
}
