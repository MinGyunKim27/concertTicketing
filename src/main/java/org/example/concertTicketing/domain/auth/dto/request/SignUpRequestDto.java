package org.example.concertTicketing.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class SignUpRequestDto {
    @NotBlank(message = "사용자 이름을 입력해주세요.")
    private String username;

    @NotBlank(message = "이메일을 입력해주세요.")
    @Email
    private String email;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",
            message = "비밀번호는 최소 8글자 이상, 대소문자 하나 이상 포함해야합니다.")
    private String password;

    @NotBlank(message = "닉네임을 입력해주세요.")
    private String nickname;

    @NotBlank(message = "역할은 필수입니다.")
    private String userRole;

    public SignUpRequestDto(String username, String email, String password, String nickname, String userRole) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.userRole = userRole;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getNickname() {
        return nickname;
    }

    public String getUserRole() {
        return userRole;
    }
}
