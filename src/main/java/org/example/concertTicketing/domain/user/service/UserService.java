package org.example.concertTicketing.domain.user.service;


import org.example.concertTicketing.domain.user.dto.request.UserUpdateRequestDto;
import org.example.concertTicketing.domain.user.dto.response.UserProfileResponseDto;
import org.example.concertTicketing.domain.user.dto.response.UserUpdateResponseDto;
import org.example.concertTicketing.domain.user.entity.User;
import org.example.concertTicketing.domain.user.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {
    // 속성
    private final UserRepository userRepository;

    // 생성자
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 기능
    // 본인 프로필 조회
    public UserProfileResponseDto getMyProfile(Long userId) {
        // 1. 데이터 준비 - 필요없음

        // 2. 조회 - 삭제된 데이터 조회 안되게!
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));

        // 3. 응답 dto 만들기
        UserProfileResponseDto responseDto = UserProfileResponseDto.builder()
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
    @Transactional
    // 본인 프로필 수정
    public UserUpdateResponseDto userUpdateMyProfile(
            Long userId,
            UserUpdateRequestDto requestDto
    ) {
        // 1. 데이터 준비
        String username = requestDto.getUsername();
        String nickname = requestDto.getNickname();

        // 2. 조회
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));

        // 3. 업데이트
        User updateUser = findUser.updateUsernameAndNickname(username, nickname);

        // 4. 응답 dto 만들기
        UserUpdateResponseDto updateResponseDto = UserUpdateResponseDto.builder()
                .id(updateUser.getId())
                .username(updateUser.getUsername())
                .email(updateUser.getEmail())
                .nickname(updateUser.getNickname())
                .userRole(updateUser.getUserRole())
                .createdAt(updateUser.getCreatedAt())
                .build();

        // 5. dto 반환
        return updateResponseDto;
    }

}
