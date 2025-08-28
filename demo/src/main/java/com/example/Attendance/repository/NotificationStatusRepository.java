package com.example.Attendance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.Attendance.entity.NotificationStatus;
import com.example.Attendance.entity.User;
import com.example.Attendance.entity.Notification;

import java.util.*;
import java.lang.*;

public interface NotificationStatusRepository extends JpaRepository<NotificationStatus, Long>{

	Optional<NotificationStatus> findByNotificationAndUsers(Notification notification, User user);
	
	List<NotificationStatus> findByUsersOrderByStatusIdDesc(User user);
	
	@Modifying
	@Query("DELETE FROM NotificationStatus ns WHERE ns.notification.notiId = :notiId")
	void deleteAllById(@Param("notiId") Long notiId);
	
}
