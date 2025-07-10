package org.example.concertTicketing.domain.user.controller;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.example.concertTicketing.domain.common.dto.CommonResponseDto;
import org.example.concertTicketing.domain.common.dto.PagedResponse;
import org.example.concertTicketing.domain.user.dto.request.UpdateUserProfileByAdminRequestDto;
import org.example.concertTicketing.domain.user.dto.response.*;
import org.example.concertTicketing.domain.user.service.UserAdminService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Builder
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/users")
public class UserAdminController {
    private final UserAdminService userAdminService;

    // 기능
    // 관리자 -> 사용자 프로필 조회
    @GetMapping("/{userId}")
    public ResponseEntity<CommonResponseDto<UserProfileByAdminResponseDto>> getUserProfileByAdmin(@PathVariable Long userId) {
        UserProfileByAdminResponseDto responseDto = userAdminService.getUserProfile(userId);

        CommonResponseDto<UserProfileByAdminResponseDto> response = CommonResponseDto.<UserProfileByAdminResponseDto>builder()
                .success(true)
                .message("프로필 조회가 완료되었습니다.")
                .data(responseDto)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    // 관리자 -> 사용자 리스트 조회
    @GetMapping
    public ResponseEntity<CommonResponseDto<PagedResponse<UserResponseDto>>> getUserListByAdmin(
            @RequestParam(required = false) String username,
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        PagedResponse<UserResponseDto> responseDto = userAdminService.getUserList(username, pageable);

        CommonResponseDto<PagedResponse<UserResponseDto>> response = CommonResponseDto.<PagedResponse<UserResponseDto>>builder()
                .success(true)
                .message("프로필 조회가 완료되었습니다.")
                .data(responseDto)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    // 관리자용 사용자 프로필 수정
    @PatchMapping("/{userId}")
    public ResponseEntity<CommonResponseDto<UpdateUserProfileByAdminResponseDto>> updateUserProfileByAdmin(
            @PathVariable Long userId,
            @RequestBody UpdateUserProfileByAdminRequestDto requestDto
    ) {
        UpdateUserProfileByAdminResponseDto responseDto = userAdminService.updateUserProfile(userId, requestDto);

        CommonResponseDto<UpdateUserProfileByAdminResponseDto> response = CommonResponseDto.<UpdateUserProfileByAdminResponseDto>builder()
                .success(true)
                .message("프로필 수정이 완료되었습니다.")
                .data(responseDto)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }
}
