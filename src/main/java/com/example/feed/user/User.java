package com.example.feed.user;

import com.example.feed.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean isDeleted = false;

    public void delete() {
        this.isDeleted = true;
    }

    public void updateProfile(String userName, String email) {
        this.userName = userName;
        this.email = email;
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    private boolean isValidPasswordFormat(String password) {
        // 8자 이상, 영문, 숫자, 특수문자 포함
        return password.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&]).{8,}$");
    }
}
