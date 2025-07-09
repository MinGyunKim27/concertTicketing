package org.example.concertTicketing.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.example.concertTicketing.domain.user.UserRole;
import org.example.concertTicketing.domain.user.entity.User;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
public class UserResponseDto {
    private Long id;
    private String username;
    private String email;
    private String nickname;
    private UserRole userRole;
    private LocalDateTime createdAt;

    public UserResponseDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.userRole = user.getUserRole();
        this.createdAt = user.getCreatedAt();
    }

    public static UserResponseDto of(User user) {
        return new UserResponseDto(user);
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
