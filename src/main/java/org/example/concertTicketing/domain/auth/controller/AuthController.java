package org.example.concertTicketing.domain.auth.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.concertTicketing.domain.auth.dto.request.SignInRequestDto;
import org.example.concertTicketing.domain.auth.dto.request.SignUpRequestDto;
import org.example.concertTicketing.domain.auth.dto.response.SignInResponseDto;
import org.example.concertTicketing.domain.auth.dto.response.SignUpResponseDto;
import org.example.concertTicketing.domain.auth.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/auth/signup")
    public SignUpResponseDto signup(@Valid @RequestBody SignUpRequestDto signupRequest) {
        return authService.signup(signupRequest);
    }

    @PostMapping("/auth/signin")
    public SignInResponseDto signin(@Valid @RequestBody SignInRequestDto signInRequest) {
        return authService.signin(signInRequest);
    }
}
