package com.example.Attendance.config;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.Attendance.entity.User;
import com.example.Attendance.repository.UserRepository;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter{
	
	private final JwtTokenProvider jwtTokenProvider;
	private final UserRepository userRepository;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request,
									HttpServletResponse response,
									FilterChain filterChain)
			throws ServletException, IOException {
		
		String header = request.getHeader("Authorization");
		
		if(header != null && header.startsWith("Bearer ")) {
			String token = header.substring(7); //"Bearer " 이후 토큰만 추출
			
			try {
				if(jwtTokenProvider.validateToken(token))
				{
					String email = jwtTokenProvider.getEmail(token);
					User user = userRepository.findByEmail(email).orElse(null);
					
					if(user != null)
					{
						List<SimpleGrantedAuthority> authorities = Collections.singletonList(
								new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
						);
						
						UsernamePasswordAuthenticationToken authentication =
								new UsernamePasswordAuthenticationToken(email, null, authorities);
						
						authentication.setDetails(
								new WebAuthenticationDetailsSource().buildDetails(request));
						SecurityContextHolder.getContext().setAuthentication(authentication);
								
					}
				}
			} catch (JwtException e) {
				// JWT 예외 (만료, 위조) 발생 시 인증 실패로 간주
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}
			
			String email = jwtTokenProvider.getEmail(token);
		}
		
		//다음 필터로 요청 전달
		filterChain.doFilter(request, response);
	}
	
	
	
	

}
