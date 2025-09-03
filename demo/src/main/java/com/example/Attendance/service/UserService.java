package com.example.Attendance.service;

import com.example.Attendance.entity.User;
import com.example.Attendance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public String getNameByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(User::getName)
                .orElse(null);
    }
}
