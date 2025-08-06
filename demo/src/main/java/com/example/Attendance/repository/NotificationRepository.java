package com.example.Attendance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
import java.lang.*;
import com.example.Attendance.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long>{
}
