package com.example.dividend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.StaxUtils;

import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TokenProvider {
        private static final long TOKEN_EXPIRE_TIME = 1000 * 60 * 60;//1시간
        private static final String KEY_ROLES= "roles";
        @Value("${spring.jwt.secret}")
        private String secretKey;

        /**
         * 토큰생서 (발급)
         * @param username
         * @param roles
         * @return
         */
        public String generateToken(String username, List<String > roles){
                Claims claims = Jwts.claims().setSubject(username);
                claims.put(KEY_ROLES,roles);

                Date now = new Date();
                Date expiredDate = new Date(now.getTime() + TOKEN_EXPIRE_TIME);//토큰 만료시간 설정

                return Jwts.builder()
                        .setClaims(claims)
                        .setIssuedAt(now)//토큰 생성시간
                        .setExpiration(expiredDate)//만료시간
                        .signWith(SignatureAlgorithm.ES512,this.secretKey)//사용할 암호화 알고리즘 및 비밀키
                        .compact();

        }

        public String getUsername(String token){
               return this.parseClaims(token).getSubject();
        }
        //토큰 만료 여부 확인
        public boolean validateToken(String token){
                if (!StringUtils.hasText(token)){
                        return false;
                }
                Claims claims = this.parseClaims(token);
                return !claims.getExpiration().before(new Date());
        }

        private Claims parseClaims(String token){
                try {

                   return Jwts.parser().setSigningKey(this.secretKey).parseClaimsJws(token).getBody();
                }catch (ExpiredJwtException e){
                        return e.getClaims();
                }
        }

}
