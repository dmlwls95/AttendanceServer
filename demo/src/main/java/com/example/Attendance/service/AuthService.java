package com.example.Attendance.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.Attendance.config.JwtTokenProvider;
import com.example.Attendance.dto.LoginRequest;
import com.example.Attendance.dto.LoginResponse;
import com.example.Attendance.dto.SignupRequest;
import com.example.Attendance.dto.UserResponse;
import com.example.Attendance.entity.User;
import com.example.Attendance.repository.UserRepository;

import lombok.*;


@Service
@RequiredArgsConstructor
public class AuthService {
	
	private final UserRepository userRepository;
	private final JwtTokenProvider jwtTokenProvider;
	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	private Logger log = LoggerFactory.getLogger(getClass());
	
	public UserResponse signup(SignupRequest request)
	{
		if(userRepository.existsByEmail(request.getEmail()))
		{
			throw new IllegalArgumentException("이미 존재하는 이메일 입니다.");
		}
		
		User user = User.builder()
				.name(request.getName())
				.email(request.getEmail())
				.password(passwordEncoder.encode(request.getPassword()))
				.role(User.Role.USER)
				.build();
		
		userRepository.save(user);
		
		return UserResponse.builder()
				.id(user.getId())
				.name(user.getName())
				.email(user.getEmail())
				.build();
	}
	
	
	
	public LoginResponse login(LoginRequest request)
	{
		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new IllegalArgumentException("이메일이 존재하지 않습니다"));
		
		if(!passwordEncoder.matches(request.getPassword(), user.getPassword()))
		{
			throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
		}
		log.debug(request.toString());
		String token = jwtTokenProvider.createToken(user.getEmail());
		return new LoginResponse(token, user.getRole().toString());
	}

}
