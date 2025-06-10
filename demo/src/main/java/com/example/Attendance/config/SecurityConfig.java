package com.example.Attendance.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.Attendance.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
	private final JwtTokenProvider jwtTokenProvider;
	private final UserRepository userRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
        	.cors()
        	.and()
            .csrf().disable() // CSRF 보호 비활성화 (API 서버용이니까)
            .authorizeRequests()
                .antMatchers("/auth/**", "swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            .and()
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, userRepository), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}