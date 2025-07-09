package org.example.concertTicketing.domain.user.dto.request;

public class UpdateUserProfileByAdminRequestDto {
    private String username;
    private String nickname;
    private String userRole;

    public UpdateUserProfileByAdminRequestDto(String username, String nickname, String userRole) {
        this.username = username;
        this.nickname = nickname;
        this.userRole = userRole;
    }

    public String getUsername() {
        return username;
    }

    public String getNickname() {
        return nickname;
    }

    public String getUserRole() {
        return userRole;
    }
}
