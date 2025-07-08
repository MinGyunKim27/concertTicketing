package org.example.concertTicketing.domain.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.example.concertTicketing.domain.common.dto.PagedResponse;
import org.example.concertTicketing.domain.user.dto.request.AdminUserUpdateDto;
import org.example.concertTicketing.domain.user.dto.response.UserResponseDto;
import org.example.concertTicketing.domain.user.entity.User;
import org.example.concertTicketing.domain.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Setter
@RequiredArgsConstructor
public class UserAdminService {
    private final UserRepository userRepository;

    public PagedResponse<UserResponseDto> findUserList(String username, Pageable pageable){
        if (username != null) {
            Page<User> byUsernameContaining = userRepository.findByUsernameContaining(username, pageable);
            Page<UserResponseDto> byUsernameContainingDto = byUsernameContaining.map(UserResponseDto::of);

            return PagedResponse.from(byUsernameContainingDto);
        }

        Page<User> userList = userRepository.findAll(pageable);
        Page<UserResponseDto> userResponseList = userList.map(UserResponseDto::of);

        return PagedResponse.from(userResponseList);

    }

    public UserResponseDto findUser(Long id){
        User findUser = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("사용자를 찾지 못했습니다."));
        return UserResponseDto.of(findUser);
    }

    @Transactional
    public UserResponseDto updateUser(Long id, AdminUserUpdateDto dto){
        User findUser = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("사용자를 찾지 못했습니다."));
        findUser.updateUser(dto.getUsername(), dto.getNickname());
        findUser.changeRole(dto.getUserRole());

        userRepository.save(findUser);

        return UserResponseDto.of(findUser);
    }
}
