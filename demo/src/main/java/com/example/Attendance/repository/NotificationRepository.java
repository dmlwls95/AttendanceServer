package com.example.Attendance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.Attendance.entity.Notification;
import java.util.*;
import java.lang.*;

public interface NotificationRepository extends JpaRepository<Notification, Long>{
	
	Optional<Notification> findByNotiId(Long noti_id);
}
