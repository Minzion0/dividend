package com.example.dividend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity(name = "member")
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MemberEntity implements UserDetails {

    // ID 필드이며 자동 생성 전략을 IDENTITY로 설정합니다.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;

    // 리스트를 데이터베이스에 저장할 수 있도록 변환하는 컨버터를 사용합니다.
    @Convert(converter = StringListConverter.class)
    private List<String> roles;

    // 정적 팩토리 메소드로 MemberEntity 객체를 생성합니다.
    public static MemberEntity from(String username, String password, List<String> roles) {
        return new MemberEntity(null, username, password, roles);
    }

    // 권한을 반환하는 메소드입니다.
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
            // 스프링 시큐리티에서 지원하는 롤 관련 기능들을 사용
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
    }

    // 계정이 만료되지 않았는지 확인하는 메소드입니다.
    @Override
    public boolean isAccountNonExpired() {
        return true; // true로 수정
    }

    // 계정이 잠기지 않았는지 확인하는 메소드입니다.
    @Override
    public boolean isAccountNonLocked() {
        return true; // true로 수정
    }

    // 자격 증명이 만료되지 않았는지 확인하는 메소드입니다.
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // true로 수정
    }

    // 계정이 활성화되었는지 확인하는 메소드입니다.
    @Override
    public boolean isEnabled() {
        return true; // true로 수정
    }
}