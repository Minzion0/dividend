package com.example.dividend.security;

import com.example.dividend.model.constants.Authority;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Slf4j
@Configuration
@EnableWebSecurity
//@EnableGlobalMethodSecurity 강의에서는 해당 어노테이션을 사용했지만 현재 deprecated 됨.
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
//securedEnabled => Secured 애노테이션 사용 여부, prePostEnabled => PreAuthorize 어노테이션 사용 여부.
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequest ->
                        authorizeRequest
                                .requestMatchers(
                                        "/auth/signup",
                                        "/auth/signin",
                                        "/h2-console/**"
                                ).permitAll()
                                .requestMatchers(HttpMethod.GET, "/company").hasAnyRole(Authority.ROLE_READ.getRole(), Authority.ROLE_WRITE.getRole())
                                .requestMatchers(HttpMethod.POST, "/company").hasRole(Authority.ROLE_WRITE.getRole())
                                .requestMatchers(HttpMethod.DELETE, "/company").hasRole(Authority.ROLE_WRITE.getRole())
                                .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, BasicAuthenticationFilter.class)
                .headers(
                        headersConfigurer ->
                                headersConfigurer.frameOptions(
                                        HeadersConfigurer.FrameOptionsConfig::sameOrigin
                                )
                );
        return http.build();
    }

    /**
     * 필터를 거치지 않는 요청 설정
     *
     * @return
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
                PathRequest.toStaticResources().atCommonLocations()
        );
    }
}