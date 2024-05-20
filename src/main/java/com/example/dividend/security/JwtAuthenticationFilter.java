package com.example.dividend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    public static final String TOKEN_HEADER="Authorization";
    public static final String TOKEN_PREFIX="Bearer ";
    private final TokenProvider tokenProvider;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = this.resolveTokenFromRequest(request);

        if (StringUtils.hasText(token) && this.tokenProvider.validateToken(token)){
            //토큰 유효성 검증
            Authentication auth = this.tokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        //다음 필터 실행을 위해 필터체인
        filterChain.doFilter(request,response);
    }

    private String resolveTokenFromRequest(HttpServletRequest request){
        String token = request.getHeader(TOKEN_HEADER);
        //토큰이 있는 경우라면 Bearer를 제외한 순수 토큰만 리턴한다
        if (!ObjectUtils.isEmpty(token) && token.startsWith(TOKEN_PREFIX)){
            return token.substring(TOKEN_PREFIX.length());
        }
        //만약 비어 있다면 null
        return null;
    }
}
