package org.example.concertTicketing.domain.auth.service;

import org.example.concertTicketing.config.JwtService;
import org.example.concertTicketing.config.PasswordEncoder;
import org.example.concertTicketing.domain.auth.dto.request.SignInRequestDto;
import org.example.concertTicketing.domain.auth.dto.request.SignUpRequestDto;
import org.example.concertTicketing.domain.auth.dto.response.SignInResponseDto;
import org.example.concertTicketing.domain.auth.dto.response.SignUpResponseDto;
import org.example.concertTicketing.domain.user.UserRole;
import org.example.concertTicketing.domain.user.entity.User;
import org.example.concertTicketing.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    // 회원가입
    public SignUpResponseDto signupService(SignUpRequestDto requestDto) {
        // 1. 데이터 준비
        String username = requestDto.getUsername();
        String email = requestDto.getEmail();
        String password = requestDto.getPassword();
        String nickname = requestDto.getNickname();

        // 1-2. 검증 로직
        // 이메일 중복
        boolean existUserEmail = userRepository.existsByEmail(email);
        if(existUserEmail) {
            throw new RuntimeException("이메일이 중복 되었습니다.");
        }
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(password);

        UserRole userRole = UserRole.of(requestDto.getUserRole());

        // 2. 엔티티 만들기 (저장하려고)
        User user = User.builder()
                .username(username)
                .email(email)
                .password(encodedPassword)
                .nickname(nickname)
                .userRole(userRole)
                .build();

        // 3. 저장
        User savedUser = userRepository.save(user);

        // 4. 응답 dto 만들기
        SignUpResponseDto responseDto = SignUpResponseDto.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .nickname(savedUser.getNickname())
                .userRole(savedUser.getUserRole())
                .createdAt(savedUser.getCreatedAt())
                .build();

        // 5. dto 반환
        return responseDto;
    }
    @Transactional
    // 로그인
    public SignInResponseDto signinService(SignInRequestDto requestDto) {
        // 1. 데이터 준비
        String username = requestDto.getUsername();
        String password = requestDto.getPassword();

        // 2. 조회
        // 로그인용 사용자이름으로 회원 확인
        User findUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자명입니다."));

        // 3. 검증
        // 비밀번호 일치하면 로그인, 일치안하면 X
        boolean matchedPassword = passwordEncoder.matches(password, findUser.getPassword());
        if(!matchedPassword) {
            throw new RuntimeException("비밀번호가 잘못되었습니다.");
        }

        // 4. 로그인 성공시 토큰 발급
        String jwtToken = jwtService.createJwt(findUser.getId(), String.valueOf(findUser.getUserRole()));

        // 5. 응답 dto 만들기
        SignInResponseDto responseDto = SignInResponseDto.builder()
                .jwtToken(jwtToken)
                .build();

        // 6. dto 반환
        return responseDto;
    }
}
