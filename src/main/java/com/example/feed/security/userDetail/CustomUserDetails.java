package com.example.feed.security.userDetail;

import com.example.feed.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList(); // or ROLE_USER 권한 부여 가능
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    // 로그인 ID: 이메일 사용
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정 만료 여부
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정 잠김 여부
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 비밀번호 만료 여부
    }

    @Override
    public boolean isEnabled() {
        return true; // 계정 활성화 여부
    }

    // User 엔티티의 정보에 직접 접근하고 싶을 때 사용
    public String getUserName() {
        return user.getUserName();
    }

    public Long getUserId() {
        return user.getId();
    }
}
