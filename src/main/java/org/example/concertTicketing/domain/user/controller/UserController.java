package org.example.concertTicketing.domain.user.controller;


import lombok.RequiredArgsConstructor;
import org.example.concertTicketing.domain.common.dto.CommonResponseDto;
import org.example.concertTicketing.domain.user.dto.request.UserDeleteRequestDto;
import org.example.concertTicketing.domain.user.dto.request.UserUpdateRequestDto;
import org.example.concertTicketing.domain.user.dto.response.UserDeleteResponseDto;
import org.example.concertTicketing.domain.user.dto.response.UserProfileResponseDto;
import org.example.concertTicketing.domain.user.dto.response.UserUpdateResponseDto;
import org.example.concertTicketing.domain.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    // 속성
    private final UserService userService;

    // 기능
    // 본인 프로필 조회
    @GetMapping("/my")
    public ResponseEntity<CommonResponseDto<UserProfileResponseDto>> getMyProfileByUser(@AuthenticationPrincipal Long userId) {
        UserProfileResponseDto responseDto = userService.getMyProfile(userId);

        CommonResponseDto<UserProfileResponseDto> response = CommonResponseDto.<UserProfileResponseDto>builder()
                .success(true)
                .message("프로필 조회가 완료되었습니다.")
                .data(responseDto)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    // 본인 프로필 수정
    @PatchMapping("/my")
    public ResponseEntity<CommonResponseDto<UserUpdateResponseDto>> updateMyProfile(
            @AuthenticationPrincipal Long userId,
            @RequestBody UserUpdateRequestDto requestDto
    ) {
        UserUpdateResponseDto responseDto = userService.userUpdateMyProfile(userId, requestDto);

        CommonResponseDto<UserUpdateResponseDto> response = CommonResponseDto.<UserUpdateResponseDto>builder()
                .success(true)
                .message("프로필 수정이 완료되었습니다.")
                .data(responseDto)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    // 회원 탈퇴
    @DeleteMapping("/withdraw")
    public ResponseEntity<CommonResponseDto<UserDeleteResponseDto>> deleteUser(
            @AuthenticationPrincipal Long userId,
            @RequestBody UserDeleteRequestDto requestDto
    ) {
        CommonResponseDto<UserDeleteResponseDto> response = CommonResponseDto.<UserDeleteResponseDto>builder()
                .success(true)
                .message("회원 탈퇴가 완료되었습니다.")
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }



    // 시큐리티 테스트 코드
//    @GetMapping("/test")
//    public String test(Authentication authentication) {
//        Long userId = (Long) authentication.getPrincipal();
//        return "인증된 사용자 ID: " + userId;
//    }
//
//    @GetMapping("/test-open")
//    public String testOpen() {
//        return "인증없이 접근 가능";
//    }
}
