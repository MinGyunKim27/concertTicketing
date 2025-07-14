package org.example.concertTicketing.domain.user.service;

import org.example.concertTicketing.config.PasswordEncoder;
import org.example.concertTicketing.domain.user.dto.request.UserDeleteRequestDto;
import org.example.concertTicketing.domain.user.dto.request.UserUpdateRequestDto;
import org.example.concertTicketing.domain.user.dto.response.UserProfileResponseDto;
import org.example.concertTicketing.domain.user.dto.response.UserUpdateResponseDto;
import org.example.concertTicketing.domain.user.entity.User;
import org.example.concertTicketing.domain.user.UserRole;
import org.example.concertTicketing.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    public UserServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("내 프로필 조회 성공")
    void testGetMyProfileSuccess() {
        // Given
        Long userId = 1L;
        User user = User.builder()
                .id(userId)
                .username("testuser")
                .email("test@example.com")
                .nickname("테스터")
                .userRole(UserRole.USER)
                .isDeleted(false)
                .build();

        given(userRepository.findByIdAndIsDeletedFalse(userId)).willReturn(Optional.of(user));

        // When
        UserProfileResponseDto profile = userService.getMyProfile(userId);

        // Then
        assertThat(profile).isNotNull();
        assertThat(profile.getUsername()).isEqualTo("testuser");
        assertThat(profile.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("내 프로필 조회 실패 - 삭제된 사용자")
    void testGetMyProfileFail_UserNotFound() {
        // Given
        Long userId = 1L;
        given(userRepository.findByIdAndIsDeletedFalse(userId)).willReturn(Optional.empty());

        // When & Then
        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.getMyProfile(userId));
        assertThat(ex.getMessage()).contains("사용자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("내 프로필 수정 성공")
    void testUpdateMyProfileSuccess() {
        // Given
        Long userId = 1L;

        User user = User.builder()
                .id(userId)
                .username("oldName")
                .nickname("oldNick")
                .email("email@test.com")
                .userRole(UserRole.USER)
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        UserUpdateRequestDto request = new UserUpdateRequestDto("newName", "newNick");

        // When
        UserUpdateResponseDto response = userService.userUpdateMyProfile(userId, request);

        // Then
        assertThat(response.getUsername()).isEqualTo("newName");
        assertThat(response.getNickname()).isEqualTo("newNick");
    }

    @Test
    @DisplayName("회원 탈퇴 성공")
    void testDeleteUserSuccess() {
        // Given
        Long userId = 1L;
        String rawPassword = "password123";
        String encodedPassword = "encodedPassword";

        User user = User.builder()
                .id(userId)
                .username("test")
                .password(encodedPassword)
                .build();

        UserDeleteRequestDto requestDto = new UserDeleteRequestDto(rawPassword);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(rawPassword, encodedPassword)).willReturn(true);

        // When
        userService.deleteUserService(userId, requestDto);

        // Then
        assertThat(user.isDeleted()).isTrue(); // softDelete() 호출 시 true가 되도록 구현되었어야 함
    }

    @Test
    @DisplayName("회원 탈퇴 실패 - 비밀번호 불일치")
    void testDeleteUserFail_InvalidPassword() {
        // Given
        Long userId = 1L;
        String rawPassword = "wrongPassword";

        User user = User.builder()
                .id(userId)
                .username("test")
                .password("encodedPassword")
                .build();

        UserDeleteRequestDto requestDto = new UserDeleteRequestDto(rawPassword);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(rawPassword, user.getPassword())).willReturn(false);

        // When & Then
        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.deleteUserService(userId, requestDto));
        assertThat(ex.getMessage()).contains("비밀번호가 일치하지 않습니다.");
    }
}
