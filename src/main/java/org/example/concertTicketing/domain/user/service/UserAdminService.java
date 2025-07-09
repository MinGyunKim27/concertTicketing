package org.example.concertTicketing.domain.user.service;

import org.example.concertTicketing.config.PasswordEncoder;
import org.example.concertTicketing.domain.common.dto.PagedResponse;
import org.example.concertTicketing.domain.user.UserRole;
import org.example.concertTicketing.domain.user.dto.request.UpdateUserProfileByAdminRequestDto;
import org.example.concertTicketing.domain.user.dto.response.*;
import org.example.concertTicketing.domain.user.entity.User;
import org.example.concertTicketing.domain.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserAdminService {
    private final UserRepository userRepository;

    public UserAdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    // 기능
    // 관리자 -> 사용자 프로필 조회
    public UserProfileByAdminResponseDto getUserProfile(Long userId) {
        // 1. 데이터 준비 - 필요없음

        // 2. 조회
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 3. 응답 dto 만들기
        UserProfileByAdminResponseDto responseDto = UserProfileByAdminResponseDto.builder()
                .id(findUser.getId())
                .username(findUser.getUsername())
                .email(findUser.getEmail())
                .nickname(findUser.getNickname())
                .userRole(findUser.getUserRole())
                .createdAt(findUser.getCreatedAt())
                .build();

        // 4. dto 반환
        return responseDto;
    }
    // 관리자 -> 사용자 리스트 조회
    public PagedResponse<UserResponseDto> getUserList(String username, Pageable pageable) {
        // 1. 데이터 준비 = 필요없음.

        // 2. 리스트 조회 - username으로 필터링된 리스트
        Page<User> userPage;
        if(username == null) {
            userPage = userRepository.findAll(pageable);
        } else {
            userPage = userRepository.findByUsernameContaining(username, pageable);
        }

        // 3. 응답 dto 만들기
        Page<UserResponseDto> userDtoPage = userPage.map(UserResponseDto::of);

        // 4. dto 반환
        return PagedResponse.from(userDtoPage);
    }
    // 관리자용 사용자 프로필 수정
    public UpdateUserProfileByAdminResponseDto updateUserProfile(
            Long userId,
            UpdateUserProfileByAdminRequestDto requestDto
        ) {
        // 1. 데이터 준비
        String username = requestDto.getUsername();
        String nickname = requestDto.getNickname();
        String userRole = requestDto.getUserRole();

        // 2. 조회
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 3. 업데이트
        User updateUser = findUser.updateUserByAdmin(username, nickname, UserRole.valueOf(userRole));

        // 4. 응답 dto 만들기
        UpdateUserProfileByAdminResponseDto responseDto = UpdateUserProfileByAdminResponseDto.builder()
                .id(updateUser.getId())
                .username(updateUser.getUsername())
                .email(updateUser.getEmail())
                .nickname(updateUser.getNickname())
                .userRole(updateUser.getUserRole())
                .createdAt(updateUser.getCreatedAt())
                .build();

        // 5. dto 반환
        return responseDto;
    }
}
