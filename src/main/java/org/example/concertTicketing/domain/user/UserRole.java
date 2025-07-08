package org.example.concertTicketing.domain.user;

import org.example.concertTicketing.domain.common.exception.InvalidRequestException;

import java.util.Arrays;

public enum UserRole {
    ADMIN, USER;

    public static UserRole of(String role) {
        if (role == null) {
            throw new InvalidRequestException("UserRole은 null일 수 없습니다.");
        }
        return Arrays.stream(UserRole.values())
                .filter(r -> r.name().equalsIgnoreCase(role))
                .findFirst()
                .orElseThrow(() -> new InvalidRequestException("유효하지 않은 UerRole"));
    }
}
