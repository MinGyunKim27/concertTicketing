package org.example.concertTicketing.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.example.concertTicketing.domain.user.entity.User;
import org.example.concertTicketing.domain.user.enums.UserRole;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
public class UserResponseDto {
    private final Long id;
    private final String username;
    private final String email;
    private final UserRole userRole;
    private final String nickname;
    private final LocalDateTime createdAt;

    public UserResponseDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.userRole = user.getUserRole();
        this.nickname = user.getNickname();
        this.createdAt = user.getCreatedAt();
    }

    public static UserResponseDto of(User user) {
        return new UserResponseDto(user);
    }
}
