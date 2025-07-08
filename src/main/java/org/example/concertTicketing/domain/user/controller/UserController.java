package org.example.concertTicketing.domain.user.controller;


import lombok.RequiredArgsConstructor;
import org.example.concertTicketing.domain.common.dto.CommonResponseDto;
import org.example.concertTicketing.domain.user.dto.request.AdminUserUpdateDto;
import org.example.concertTicketing.domain.user.dto.request.UserUpdateDto;
import org.example.concertTicketing.domain.user.dto.response.UserResponseDto;
import org.example.concertTicketing.domain.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/api/users/my")
    public ResponseEntity<CommonResponseDto<UserResponseDto>> myProfile(
            @AuthenticationPrincipal Long userId
    ){
        return ResponseEntity.ok(CommonResponseDto.ok("사용자 조회에 성공 했습니다.",userService.findMe(userId)));
    }

    @PatchMapping("/api/users/my")
    public ResponseEntity<CommonResponseDto<UserResponseDto>> updateMyProfile(
            @AuthenticationPrincipal Long userId,
            @RequestBody UserUpdateDto dto
            ){
        return ResponseEntity.ok(CommonResponseDto.ok("사용자 수정에 성공 했습니다.",userService.updateUser(userId,dto)));
    }
}
