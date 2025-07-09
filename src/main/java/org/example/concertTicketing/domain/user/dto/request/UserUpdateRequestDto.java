package org.example.concertTicketing.domain.user.dto.request;

public class UserUpdateRequestDto {
    private String username;
    private String nickname;

    public UserUpdateRequestDto(String username, String nickname) {
        this.username = username;
        this.nickname = nickname;
    }

    public String getUsername() {
        return username;
    }

    public String getNickname() {
        return nickname;
    }
}
