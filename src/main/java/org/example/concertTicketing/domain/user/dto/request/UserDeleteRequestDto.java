package org.example.concertTicketing.domain.user.dto.request;

public class UserDeleteRequestDto {
    private String password;

    public UserDeleteRequestDto(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
}
