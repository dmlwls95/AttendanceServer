package com.example.Attendance.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Attendance.service.NotificationStatusService;
import com.example.Attendance.dto.NormalResponse;
import com.example.Attendance.service.NotificationService;

import lombok.RequiredArgsConstructor;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notification")
public class NotificationController {
	
	private final NotificationService notificationService;
	private final NotificationStatusService notificationStatusService;
	
	@PostMapping("/{notiId}/read")
    public ResponseEntity<Void> readNotification(@PathVariable Long notiId, Principal principal) {
		String userLoginId= principal.getName();
        notificationStatusService.readNotification(notiId, userLoginId);
        return ResponseEntity.ok().build();
    }
	
	@DeleteMapping("/{notiId}/delete")
	public boolean deleteNotification(@PathVariable Long notiId) {
		System.out.println("삭제 함수");
		return notificationService.deleteNotification(notiId);
	}
	
}
