package org.example.concertTicketing.domain.auth.dto.request;

public class SignInRequestDto {
    private String username;
    private String password;

    public SignInRequestDto(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
