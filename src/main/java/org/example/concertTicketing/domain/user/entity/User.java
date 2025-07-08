package org.example.concertTicketing.domain.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.concertTicketing.domain.common.entity.Timestamped;
import org.example.concertTicketing.domain.user.UserRole;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users") // 테이블명은 "users"
@Builder
public class User extends Timestamped {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole userRole;

    // isDelete - ture : 삭제, false : 삭제안됨
    private boolean isDeleted = false;

    @Builder
    public User(String username, String email, String password, String nickname, UserRole userRole) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.userRole = userRole;
    }

    public User updateUsernameAndNickname(String username, String nickname) {
        this.username = username;
        this.nickname = nickname;

        return this;
    }

    public void softDelete() {
        this.isDeleted = true;
    }
}
