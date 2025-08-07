package com.example.Attendance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.Attendance.entity.NotificationStatus;
import java.util.*;
import java.lang.*;

public interface NotificationStatusRepository extends JpaRepository<NotificationStatus, Long>{

}
