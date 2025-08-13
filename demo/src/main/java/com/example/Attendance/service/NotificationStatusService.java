package com.example.Attendance.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import com.example.Attendance.repository.NotificationRepository;
import com.example.Attendance.repository.NotificationStatusRepository;
import com.example.Attendance.repository.UserRepository;

import com.example.Attendance.entity.Notification;
import com.example.Attendance.entity.NotificationStatus;
import com.example.Attendance.entity.User;

@Service
@RequiredArgsConstructor
public class NotificationStatusService {
	
	private final NotificationRepository notificationRepository;
	private final NotificationStatusRepository notificationStatusRepository;
    private final UserRepository userRepository;
    
    public void readNotification(Long notification_id, String email) {
		
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("유저 없음"));
		
		Notification notification = notificationRepository.findByNotiId(notification_id)
				.orElseThrow(() -> new RuntimeException("공지 없음"));
		
		NotificationStatus status = notificationStatusRepository.findByNotificationAndUsers(notification, user)
				.orElseThrow(() -> new RuntimeException("알람 없음"));
		
		status.setIsRead(NotificationStatus.ReadType.READ);
		
		notificationStatusRepository.save(status);
	}
}
