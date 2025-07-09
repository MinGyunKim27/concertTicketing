package org.example.concertTicketing.domain.user.service;


import org.example.concertTicketing.config.PasswordEncoder;
import org.example.concertTicketing.domain.user.dto.request.UserDeleteRequestDto;
import org.example.concertTicketing.domain.user.dto.request.UserUpdateRequestDto;
import org.example.concertTicketing.domain.user.dto.response.UserDeleteResponseDto;
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
    private final PasswordEncoder passwordEncoder;

    // 생성자
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 기능
    // 본인 프로필 조회
    public UserProfileResponseDto getMyProfile(Long userId) {
        // 1. 데이터 준비 - 필요없음

        // 2. 조회 - 삭제된 데이터 조회 안되게!
        User findUser = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

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
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

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
    // 회원 탈퇴
    public UserDeleteResponseDto deleteUserService(
            Long userId,
            UserDeleteRequestDto requestDto
    ) {
        // 1. 데이터 준비
        String password = requestDto.getPassword();

        // 2. 조회
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 3. 삭제
        if(!passwordEncoder.matches(password, findUser.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }
        findUser.softDelete();

        // 4. 응답 dto 만들기 (서비스-컨트롤러 구조 일관성 있게 유지하기위해 @Builder 만 있는 빈 DTO 만듬)
//        UserDeleteResponseDto responseDto = UserDeleteResponseDto.builder().build();

        // 5. dto 반환
        return null;
    }
}
