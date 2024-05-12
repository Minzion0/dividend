package com.example.dividend.service;

import com.example.dividend.model.Auth;
import com.example.dividend.model.MemberEntity;
import com.example.dividend.persist.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.memberRepository.findAllByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("couldn't find user ->" + username));
    }

    /**
     * 회원가입
     * @param member
     * @return
     */
    public MemberEntity register(Auth.SignUp member){
        boolean exists = this.memberRepository.existsByUsername(member.getUsername());
        if (exists){
            throw new RuntimeException("이미 사용 중인 아이디 입니다.");
        }

        member.setPassword(passwordEncoder.encode(member.getPassword()));

        return this.memberRepository.save(member.toEntity());
    }

    /**
     * 로그인
     * @param member
     * @return
     */
    public MemberEntity authenticate(Auth.SignIn member){

        return null;
    }
}
