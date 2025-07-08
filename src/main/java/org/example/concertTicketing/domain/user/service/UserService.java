package org.example.concertTicketing.domain.user.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.concertTicketing.domain.user.dto.request.UserUpdateDto;
import org.example.concertTicketing.domain.user.dto.response.UserResponseDto;
import org.example.concertTicketing.domain.user.entity.User;
import org.example.concertTicketing.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    public UserResponseDto findMe(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

        return UserResponseDto.of(user);
    }

    @Transactional
    public UserResponseDto updateUser(Long id, UserUpdateDto dto){
        User findUser = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("사용자를 찾지 못했습니다."));
        findUser.updateUser(dto.getUsername(),dto.getNickname());

        userRepository.save(findUser);

        return UserResponseDto.of(findUser);
    }
}
