package org.example.concertTicketing.domain.auth.dto.response;

import lombok.Builder;
import org.example.concertTicketing.domain.user.UserRole;

import java.time.LocalDateTime;

@Builder
public class SignUpResponseDto {
    private Long id;
    private String username;
    private String email;
    private String nickname;
    private UserRole userRole;
    private LocalDateTime createdAt;

    public SignUpResponseDto(Long id, String username, String email, String nickname, UserRole userRole, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.nickname = nickname;
        this.userRole = userRole;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getNickname() {
        return nickname;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
