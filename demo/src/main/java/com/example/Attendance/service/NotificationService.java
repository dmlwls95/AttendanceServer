package com.example.Attendance.service;

import java.util.*;
import java.time.format.*;

import lombok.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.messaging.simp.*;

import com.example.Attendance.repository.NotificationRepository;
import com.example.Attendance.repository.NotificationStatusRepository;
import com.example.Attendance.repository.UserRepository;

import com.example.Attendance.entity.User;
import com.example.Attendance.entity.Board;
import com.example.Attendance.entity.Notification;
import com.example.Attendance.entity.NotificationStatus;
import com.example.Attendance.entity.NotificationStatus.ReadType;
import com.example.Attendance.dto.NotificationDTO;


@Service
@RequiredArgsConstructor
public class NotificationService {
	
	private final NotificationRepository notificationRepository;
	private final NotificationStatusRepository notificationstatusRepository;
	private final UserRepository userRepository;
	private final SimpMessagingTemplate messagingTemplate;
	
	public void createNotification(Board board) {
		Notification noti = saveNotificationInfo(board);
		
		sendNotification(noti);
	}
	
	private Notification saveNotificationInfo(Board board) {
		
		// Notification 생성 후 board 정보 set, Repository에 저장
		Notification noti = new Notification();
		noti.setBoard(board);
		notificationRepository.save(noti);
		
		// User 정보 가져와서 List에 저장
		List<User> user_List = userRepository.findAll();
		
		List<NotificationStatus> notiStatus_List = new ArrayList<NotificationStatus>();
		
		// 각 User마다 알람을 관리하기 위해서
		// NotificationStatus 정보를 user 마다 저장
		for(User user : user_List) {
			NotificationStatus notiStatus = new NotificationStatus();
			
			// NotificationStatus에 알림, user, 읽음 여부(default false) 저장
			notiStatus.setNotification(noti);
			notiStatus.setUsers(user);
			notiStatus.setIsRead(NotificationStatus.ReadType.NOTREAD);
			
			// List에 담아서 DB에 한번에 저장 -> 성능 향상
			notiStatus_List.add(notiStatus);
			
			notificationstatusRepository.saveAll(notiStatus_List);
		}
		
		return noti;
	}
	
	private void sendNotification(Notification noti) {
		
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
		NotificationStatus notiStatus = new NotificationStatus();
		
		NotificationDTO dto = new NotificationDTO();
		dto.setId(noti.getNotiId());
		dto.setBoardid(noti.getBoard().getId());
		dto.setTitle(noti.getBoard().getTitle());
		dto.setWriteDate(noti.getBoard().getWriteDate().format(format));
		dto.setWriter(noti.getBoard().getWriter());
		dto.setIsRead(NotificationStatus.ReadType.NOTREAD);
		List<User> user_List = userRepository.findAll();
		
		for(User user : user_List) {
			messagingTemplate.convertAndSendToUser(
					user.getEmail(),
					"/queue/notification", 
					dto);
		}
	}
	
	public List<NotificationDTO> initNotification(String email) {
		
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
		List<NotificationDTO> dto = new ArrayList<NotificationDTO>();
		
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
		
		List<NotificationStatus> notificationList = notificationstatusRepository.findByUsersOrderByStatusIdDesc(user);
		
		for(NotificationStatus noti : notificationList) {
			NotificationDTO temp = new NotificationDTO();
			
			temp.setId(noti.getNotification().getNotiId());
			temp.setBoardid(noti.getNotification().getBoard().getId());
			temp.setTitle(noti.getNotification().getBoard().getTitle());
			temp.setWriteDate(noti.getNotification().getBoard().getWriteDate().format(format));
			temp.setWriter(noti.getNotification().getBoard().getWriter());
			temp.setIsRead(noti.getIsRead());
			dto.add(temp);
		}
		
		
		return dto;
	}
	
	
	@Transactional
	public boolean deleteNotification(Long notiId) {
		
		notificationstatusRepository.deleteAllById(notiId);
		notificationRepository.deleteById(notiId);
		
		return false;
	}
	
}
