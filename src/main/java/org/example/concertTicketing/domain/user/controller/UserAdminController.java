package org.example.concertTicketing.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.concertTicketing.domain.common.dto.CommonResponseDto;
import org.example.concertTicketing.domain.common.dto.PagedResponse;
import org.example.concertTicketing.domain.user.dto.request.AdminUserUpdateDto;
import org.example.concertTicketing.domain.user.dto.response.UserResponseDto;
import org.example.concertTicketing.domain.user.service.UserAdminService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserAdminController {
    private final UserAdminService userAdminService;

    @GetMapping("/api/admin/users")
    public ResponseEntity<CommonResponseDto<PagedResponse<UserResponseDto>>> findUserList(
            @RequestParam(required = false) String username,
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
            ){
        return ResponseEntity.ok(CommonResponseDto.ok("사용자 리스트 조회에 성공 했습니다.",userAdminService.findUserList(username,pageable)));
    }

    @GetMapping("/api/admin/users/{userId}")
    public ResponseEntity<CommonResponseDto<UserResponseDto>> findUser(
            @PathVariable Long userId
    ){

        return ResponseEntity.ok(CommonResponseDto.ok("사용자 조회에 성공 했습니다.",userAdminService.findUser(userId)));
    }

    @PatchMapping("/api/admin/users/{userId}")
    public ResponseEntity<CommonResponseDto<UserResponseDto>> updateUser(
            @PathVariable Long userId,
            @RequestBody AdminUserUpdateDto dto
            ){

        return ResponseEntity.ok(CommonResponseDto.ok("사용자 수정에 성공 했습니다.",userAdminService.updateUser(userId,dto)));
    }

}
