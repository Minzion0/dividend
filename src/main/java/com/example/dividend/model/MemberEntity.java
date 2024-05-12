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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    @Convert(converter = StringListConverter.class)
    private List<String> roles;


    public static MemberEntity from(String username,String password,List<String> roles){
        return new MemberEntity(null,username,password,roles);
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return this.roles.stream()
                //스프링 시큐리티에서 지원하는 롤관련 기능들을 사용
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
