package org.example.concertTicketing.domain.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.concertTicketing.domain.common.entity.Timestamped;
import org.example.concertTicketing.domain.user.enums.UserRole;

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
    @Column(unique = true)
    private String username;
    @Column(unique = true)
    @Email
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private UserRole userRole;
    private String nickname;
    private boolean isDeleted;
    private LocalDateTime deletedTime;

    public void updateUser(String username, String nickname){
        this.username = username;
        this.nickname = nickname;
    }

    public void changeRole(UserRole userRole){
        this.userRole = userRole;
    }

    public User(String email,String password, UserRole userRole,String nickname,String username){
        this.email = email;
        this.password = password;
        this.userRole = userRole;
        this.nickname = nickname;
        this.username = username;
    }
}
