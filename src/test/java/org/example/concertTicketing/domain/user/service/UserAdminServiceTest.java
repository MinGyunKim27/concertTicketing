package org.example.concertTicketing.domain.user.service;

import org.example.concertTicketing.domain.common.dto.PagedResponse;
import org.example.concertTicketing.domain.user.UserRole;
import org.example.concertTicketing.domain.user.dto.request.UpdateUserProfileByAdminRequestDto;
import org.example.concertTicketing.domain.user.dto.response.UpdateUserProfileByAdminResponseDto;
import org.example.concertTicketing.domain.user.dto.response.UserProfileByAdminResponseDto;
import org.example.concertTicketing.domain.user.dto.response.UserResponseDto;
import org.example.concertTicketing.domain.user.entity.User;
import org.example.concertTicketing.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

class UserAdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserAdminService userAdminService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private User createUser(Long id) {
        return User.builder()
                .id(id)
                .username("admin")
                .nickname("관리자")
                .email("admin@example.com")
                .userRole(UserRole.ADMIN)
                .build();
    }

    @Test
    @DisplayName("관리자: 사용자 프로필 조회 성공")
    void testGetUserProfile_Success() {
        // Given
        Long userId = 1L;
        User user = createUser(userId);
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // When
        UserProfileByAdminResponseDto result = userAdminService.getUserProfile(userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(userId);
        assertThat(result.getUsername()).isEqualTo("admin");
    }

    @Test
    @DisplayName("관리자: 사용자 프로필 조회 실패 - 존재하지 않음")
    void testGetUserProfile_NotFound() {
        // Given
        Long userId = 999L;
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> userAdminService.getUserProfile(userId));
    }

    @Test
    @DisplayName("관리자: 사용자 리스트 전체 조회")
    void testGetUserList_All() {
        // Given
        Pageable pageable = PageRequest.of(0, 2);
        User user1 = createUser(1L);
        User user2 = createUser(2L);

        Page<User> userPage = new PageImpl<>(List.of(user1, user2), pageable, 2);
        given(userRepository.findAll(pageable)).willReturn(userPage);

        // When
        PagedResponse<UserResponseDto> response = userAdminService.getUserList(null, pageable);

        // Then
        assertThat(response.content()).hasSize(2);
        assertThat(response.content().get(0).getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("관리자: 사용자 리스트 검색 조회")
    void testGetUserList_WithUsername() {
        // Given
        Pageable pageable = PageRequest.of(0, 1);
        User user = createUser(3L);
        Page<User> userPage = new PageImpl<>(List.of(user), pageable, 1);
        given(userRepository.findByUsernameContaining("adm", pageable)).willReturn(userPage);

        // When
        PagedResponse<UserResponseDto> response = userAdminService.getUserList("adm", pageable);

        // Then
        assertThat(response.content()).hasSize(1);
        assertThat(response.content().get(0).getUsername()).contains("adm");
    }

    @Test
    @DisplayName("관리자: 사용자 정보 수정 성공")
    void testUpdateUserProfile_Success() {
        // Given
        Long userId = 1L;
        User user = User.builder()
                .id(userId)
                .username("old")
                .nickname("old")
                .email("admin@example.com")
                .userRole(UserRole.USER)
                .build();

        UpdateUserProfileByAdminRequestDto dto =
                new UpdateUserProfileByAdminRequestDto("newAdmin", "새관리자", "ADMIN");

        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // When
        UpdateUserProfileByAdminResponseDto response = userAdminService.updateUserProfile(userId, dto);

        // Then
        assertThat(response.getUsername()).isEqualTo("newAdmin");
        assertThat(response.getNickname()).isEqualTo("새관리자");
        assertThat(response.getUserRole()).isEqualTo(UserRole.ADMIN);
    }


    @Test
    @DisplayName("관리자: 사용자 정보 수정 실패 - 없는 사용자")
    void testUpdateUserProfile_NotFound() {
        // Given
        Long userId = 100L;
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        UpdateUserProfileByAdminRequestDto dto =
                new UpdateUserProfileByAdminRequestDto("any", "nick", "USER");

        // When & Then
        assertThrows(RuntimeException.class, () -> userAdminService.updateUserProfile(userId, dto));
    }
}
