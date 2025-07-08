package org.example.concertTicketing.domain.user.service;


import org.example.concertTicketing.domain.user.dto.response.UserProfileResponseDto;
import org.example.concertTicketing.domain.user.entity.User;
import org.example.concertTicketing.domain.user.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

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
    public UserProfileResponseDto getMyProfile(@AuthenticationPrincipal Long userId) {
        // 1. 데이터 준비
        Long id = userId;

        // 2. 조회 - 삭제된 데이터 조회 안되게!
        User findUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));

        // 3. 응답 dto 만들기
        UserProfileResponseDto responseDto = new UserProfileResponseDto(
                findUser.getId(),
                findUser.getUsername(),
                findUser.getEmail(),
                findUser.getNickname(),
                findUser.getUserRole(),
                findUser.getCreatedAt()
        );

        // 4. dto 반환
        return responseDto;
    }
    // 본인 프로필 수정


}
