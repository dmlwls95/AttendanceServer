package com.example.Attendance.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Attendance.config.EvolutionConfig;
import com.example.Attendance.dto.EvolutionData;
import com.example.Attendance.dto.GameAttendanceResultDto;
import com.example.Attendance.dto.GameMainMenuInformation;
import com.example.Attendance.entity.Attendance;
import com.example.Attendance.entity.User;
import com.example.Attendance.entity.UserMon;
import com.example.Attendance.repository.AttendanceRepository;
import com.example.Attendance.repository.UserRepository;
import com.example.Attendance.repository.UsermonRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GameService {
	
	private final AttendanceRepository attendanceRepository;
	private final UserRepository userRepository;
	private final UsermonRepository usermonRepository;
	
	private final AttendanceService attendanceService;
	private final UsermonService usermonService;
	
	@Autowired
	private EvolutionConfig evolutionConfig;
	
	public GameAttendanceResultDto checkIn(String email)
	{
		//1 출석 확인
		 boolean alreadyCheckedIn = attendanceService.hasCheckedInToday(email);
		 if(alreadyCheckedIn)
		 {
			 return new GameAttendanceResultDto(false, "이미 오늘 출석 함", 0, "Err_CheckedIn");
		 }
		 
		 //2 출석 처리
		 attendanceService.clockIn(email);
		 User user = userRepository.findByEmail(email)
				 .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 이메일 입니다."));
		 UserMon usermon = usermonRepository.findByUser(user)
				 .orElse(null);
		 if(usermon == null)
			 usermonService.registerNewUsermon(user);
		 
		 //3 보상
		 int reward = calculateDailyGummy(email);
		 usermonService.addGummy(user, reward);
		 return new GameAttendanceResultDto(true, "오늘 출석을 완료 했습니다.", reward, "Err_CheckedIn");
	}
	
	private int calculateDailyGummy(String email)
	{
		// 추후 아이템에 따른 구미 차등 지급 연속 출석 차동 지급등 구현
		return 10;
	}
	
	public GameMainMenuInformation getMainmenuInfo(String email) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalArgumentException("등록되지 않은 이메일 입니다."));
		
		UserMon usermon = usermonRepository.findByUser(user)
				.orElse(null);
		if(usermon == null)
			return null;
		
		GameMainMenuInformation gmmi = GameMainMenuInformation.builder()
				.condition(usermon.getEmotion())
				.evolveStage(usermon.getEvolve_stage())
				.monname(usermon.getMon_name())
				.monlevel(usermon.getLevel())
				.totalGummy(usermon.getGummy())
				.totalPoint(usermon.getPoint())
				.build();
		
		return gmmi;
	}
	
	public EvolutionData getRequiredEvolutionData(String email)
	{
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalArgumentException("등록되지 않은 이메일 입니다."));
		
		UserMon usermon = usermonRepository.findByUser(user)
				.orElse(null);
		
		int nextStage = usermon.getEvolve_stage() + 1;
		
		EvolutionData evoData = evolutionConfig.getData(nextStage);
		if (evoData == null) {
		    throw new IllegalStateException("진화 데이터가 존재하지 않습니다. evolveStage: " + nextStage);
		}
		
		EvolutionData result = EvolutionData.builder()
				.evolveStage(nextStage)
				.requiredGummy(evolutionConfig.getData(nextStage).getRequiredGummy())
				.requiredPoint(evolutionConfig.getData(nextStage).getRequiredPoint())
				.monName(evolutionConfig.getData(nextStage).getMonName())
				.build();
		
		return result;
	}
	
	
	public boolean tryEvolve(String email) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalArgumentException("등록되지 않은 이메일 입니다."));
		
		UserMon usermon = usermonRepository.findByUser(user)
				.orElse(null);
		
		int nextStage = usermon.getEvolve_stage() + 1;
		EvolutionData requirement = evolutionConfig.getData(nextStage);
		
		if(requirement == null) {
			throw new IllegalArgumentException("더 이상 진화할 수 없습니다.");
		}
		
		if(usermon.getGummy() < requirement.getRequiredGummy() || usermon.getPoint() < requirement.getRequiredPoint())
		{
			return false;
		}
		
		usermon.setEvolve_stage(nextStage);
		usermon.setMon_name(requirement.getMonName());
		usermon.setGummy(usermon.getGummy() - requirement.getRequiredGummy());
		usermon.setPoint(usermon.getPoint() - requirement.getRequiredPoint());
		
		usermonRepository.save(usermon);
		
		return true;
		
	}
	
}
