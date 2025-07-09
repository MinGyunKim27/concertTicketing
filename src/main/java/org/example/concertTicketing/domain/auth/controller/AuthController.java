package org.example.concertTicketing.domain.auth.controller;


import jakarta.validation.Valid;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.example.concertTicketing.domain.auth.dto.request.SignInRequestDto;
import org.example.concertTicketing.domain.auth.dto.request.SignUpRequestDto;
import org.example.concertTicketing.domain.auth.dto.response.SignInResponseDto;
import org.example.concertTicketing.domain.auth.dto.response.SignUpResponseDto;
import org.example.concertTicketing.domain.auth.service.AuthService;
import org.example.concertTicketing.domain.common.dto.CommonResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Builder
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    // 회원가입 API
    @PostMapping("/signup")
    public ResponseEntity<CommonResponseDto<SignUpResponseDto>> signup(@Valid @RequestBody SignUpRequestDto requestDto) {
        SignUpResponseDto responseDto = authService.signupService(requestDto);

        CommonResponseDto<SignUpResponseDto> response = CommonResponseDto.<SignUpResponseDto>builder()
                .success(true)
                .message("회원 가입이 완료되었습니다.")
                .data(responseDto)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    // 로그인
    @PostMapping("/signin")
    public ResponseEntity<CommonResponseDto<SignInResponseDto>> signin(@RequestBody SignInRequestDto requestDto) {
        SignInResponseDto responseDto = authService.signinService(requestDto);

        CommonResponseDto<SignInResponseDto> response = CommonResponseDto.<SignInResponseDto>builder()
                .success(true)
                .message("로그인이 완료되었습니다.")
                .data(responseDto)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }
}
