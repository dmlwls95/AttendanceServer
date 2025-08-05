package com.example.Attendance.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.Attendance.entity.User;
import com.example.Attendance.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {
	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	
	@Override
	public void run(String... args)
	{
		String adminEmail = "admin@admin.com";
		if(!userRepository.existsByEmail(adminEmail))
		{
			User admin = User.builder()
					.empnum(adminEmail)
					.email(adminEmail)
					.name("admin")
					.password(passwordEncoder.encode("q1w2e3r4"))
					.role(User.Role.ADMIN)
					.build();
			
			userRepository.save(admin);
			System.out.println("✅ 초기 관리자 계정 생성됨: " + adminEmail + " / q1w2e3r4");
        
		}
	}

}
