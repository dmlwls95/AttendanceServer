package com.example.Attendance.service;

import org.springframework.stereotype.Service;

import com.example.Attendance.entity.User;
import com.example.Attendance.entity.UserMon;
import com.example.Attendance.repository.UsermonRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsermonService {
	
	private final UsermonRepository usermonRepository;
	
	//새로운 유저몬 등록
	public void registerNewUsermon(User user)
	{
		/*
		 * private User user;
	
			private String mon_name;
			private Integer level;
			private Integer evolve_stage;
			private String emotion;
			private Integer gummy;
			private Integer point;
		 */
		UserMon usermon = UserMon.builder()
				.user(user)
				.mon_name("졸몽이")
				.level(1)
				.evolve_stage(0)
				.emotion("보통")
				.gummy(0)
				.point(0)
				.build();
		usermonRepository.save(usermon);
	}
	
	// 구미 추가
	public void addGummy(User user, int gummyCount)
	{
		//아이템 별로 구미 추가 계산
		UserMon usermon = usermonRepository.findByUser(user)
				.orElse(null);
		
		usermon.setGummy(usermon.getGummy() + gummyCount);
		usermonRepository.save(usermon);
	}
}
