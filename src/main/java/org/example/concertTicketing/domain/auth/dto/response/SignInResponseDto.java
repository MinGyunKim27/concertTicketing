package org.example.concertTicketing.domain.auth.dto.response;

import lombok.Builder;

@Builder
public class SignInResponseDto {
    private String jwtToken;

    public SignInResponseDto(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public String getJwtToken() {
        return jwtToken;
    }
}
