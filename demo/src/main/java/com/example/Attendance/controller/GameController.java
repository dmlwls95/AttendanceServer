package com.example.Attendance.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Attendance.dto.EvolutionData;
import com.example.Attendance.dto.GameAttendanceResultDto;
import com.example.Attendance.dto.GameMainMenuInformation;
import com.example.Attendance.service.GameService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
public class GameController {
	
	private final GameService gameService;
	
	@Operation(summary = "게임 사이드에서 체크인 하는 엔드 포인트")
	@GetMapping("/attendance/checkin")
	public GameAttendanceResultDto checkIn(String email)
	{
		return gameService.checkIn(email);
	}
	
	@Operation(summary = "메인메뉴 진입시 필요한 데이터 로드")
	@GetMapping("/mainmenu/info")
	public GameMainMenuInformation getMainmenuInformation(String email)
	{
		return gameService.getMainmenuInfo(email);
	}
	
	@Operation(summary = "진화 필요 데이터 로드")
	@GetMapping("/evolve/info")
	public EvolutionData getRequirementForEvolve(String email)
	{
		return gameService.getRequiredEvolutionData(email);
	}
	
	@Operation(summary = "진화 시도")
	@GetMapping("/evolve/try")
	public boolean tryEvolve(String email) {
		return gameService.tryEvolve(email);
	}
	
}
