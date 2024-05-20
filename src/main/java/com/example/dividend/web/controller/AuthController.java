package com.example.dividend.web.controller;

import com.example.dividend.model.Auth;
import com.example.dividend.model.MemberEntity;
import com.example.dividend.security.TokenProvider;
import com.example.dividend.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final MemberService memberService;
    private final TokenProvider tokenProvider;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Auth.SignUp request){
        //회원 가입을 위한 API
        MemberEntity memberResponse = this.memberService.register(request);

        return ResponseEntity.ok(memberResponse);
    }

    /**
     * 로그인 API
     * @param request
     * 아이디와 비밀번호가 일치하면 Token을 발급하여 반환 한다.
     * @return
     */
    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody Auth.SignIn request){
        //로그인 API
        MemberEntity member = this.memberService.authenticate(request);
        String token = this.tokenProvider.generateToken(member.getUsername(), member.getRoles());
        System.out.println("============================================");
        System.out.println(token);
        return ResponseEntity.ok(token);
    }
}
