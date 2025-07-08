package org.example.concertTicketing.domain.user.dto.request;

import lombok.Getter;
import org.example.concertTicketing.domain.user.enums.UserRole;

@Getter
public class AdminUserUpdateDto {
    private String username;
    private String nickname;
    private UserRole userRole;
}
