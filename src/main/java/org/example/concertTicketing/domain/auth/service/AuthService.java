package org.example.concertTicketing.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.example.concertTicketing.config.JwtUtil;
import org.example.concertTicketing.config.PasswordEncoder;
import org.example.concertTicketing.domain.auth.dto.request.SignInRequestDto;
import org.example.concertTicketing.domain.auth.dto.request.SignUpRequestDto;
import org.example.concertTicketing.domain.auth.dto.response.SignInResponseDto;
import org.example.concertTicketing.domain.auth.dto.response.SignUpResponseDto;
import org.example.concertTicketing.domain.auth.exception.AuthException;
import org.example.concertTicketing.domain.common.exception.InvalidRequestException;
import org.example.concertTicketing.domain.user.entity.User;
import org.example.concertTicketing.domain.user.enums.UserRole;
import org.example.concertTicketing.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public SignUpResponseDto signup(SignUpRequestDto signupRequest) {

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new InvalidRequestException("이미 존재하는 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());

        UserRole userRole = UserRole.of(signupRequest.getUserRole());

        User newUser = new User(
                signupRequest.getEmail(),
                encodedPassword,
                userRole,
                signupRequest.getNickname()
                ,signupRequest.getUsername()
        );
        User savedUser = userRepository.save(newUser);

        String bearerToken = jwtUtil.createToken(savedUser.getId(), savedUser.getEmail(), userRole, savedUser.getNickname());

        return new SignUpResponseDto(bearerToken);
    }

    public SignInResponseDto signin(SignInRequestDto signinRequest) {
        User user = userRepository.findByUsername((signinRequest.getUsername())).orElseThrow(
                () -> new InvalidRequestException("가입되지 않은 유저입니다."));

        // 로그인 시 이메일과 비밀번호가 일치하지 않을 경우 401을 반환합니다.
        if (!passwordEncoder.matches(signinRequest.getPassword(), user.getPassword())) {
            throw new AuthException("잘못된 비밀번호입니다.");
        }

        String bearerToken = jwtUtil.createToken(user.getId(), user.getEmail(), user.getUserRole(),user.getNickname());

        return new SignInResponseDto(bearerToken);
    }
}