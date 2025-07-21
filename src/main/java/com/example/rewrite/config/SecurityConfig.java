package com.example.rewrite.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher; // ★ AntPathRequestMatcher 임포트!

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // 개발/테스트 시 임시 비활성화 유지 or 실제 서비스 시 활성화 고려

                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                new AntPathRequestMatcher("/notice/noticeWrite"),
                                new AntPathRequestMatcher("/admin/**")
                        ).hasRole("ADMIN") // "ADMIN" 역할을 가진 사용자만 허용 (ROLE_ 접두사는 자동으로 처리됨)

                        .requestMatchers(
                                new AntPathRequestMatcher("/api/admin/**"),
                                new AntPathRequestMatcher("/admin/**")
                        ).hasRole("ADMIN")
                        .requestMatchers(
                                new AntPathRequestMatcher("/api/auth/**") // <--- 이 부분이 핵심!
                        ).permitAll()

                        // 2. 다른 경로들도 마찬가지로 수정합니다.
                        .requestMatchers(
                                new AntPathRequestMatcher("/"),
                                new AntPathRequestMatcher("/home"),
                                new AntPathRequestMatcher("/css/**"),
                                new AntPathRequestMatcher("/js/**"),
                                new AntPathRequestMatcher("/images/**"),
                                new AntPathRequestMatcher("/favicon.ico")
                                // 필요하다면 공개 API 경로 패턴 추가
                                // new AntPathRequestMatcher("/api/public/**")
                        ).permitAll()
                        .anyRequest().permitAll() // 또는 .anyRequest().authenticated()
                )

                .formLogin(formLogin -> formLogin.disable()) // 커스텀 로그인 사용 시 필수
                .httpBasic(httpBasic -> httpBasic.disable())
                .logout(logout -> logout // 로그아웃 구현
                        .logoutUrl("/logout") //여기로 보내면
                        .logoutSuccessUrl("/") //여기로 보내고
                        .invalidateHttpSession(true) // 세션 부순다
                        .permitAll() //모든 유저한테
                );

        return http.build();
    }
}