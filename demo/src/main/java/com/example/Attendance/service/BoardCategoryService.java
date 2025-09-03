package com.example.Attendance.service;

import com.example.Attendance.entity.BoardCategory;
import com.example.Attendance.entity.BoardType;
import com.example.Attendance.repository.BoardCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardCategoryService {

    private final BoardCategoryRepository boardCategoryRepository;

    public List<BoardCategory> getAllCategories() {
        return boardCategoryRepository.findAll();
    }

    public BoardCategory getCategory(BoardType boardType) {
        return boardCategoryRepository.findById(boardType)
                .orElse(BoardCategory.builder()
                        .boardType(boardType)
                        .name(boardType.name())
                        .writePermissionAdminOnly(false)
                        .build());
    }

    @Transactional
    public BoardCategory updateCategory(BoardType boardType, String name, boolean writePermissionAdminOnly) {
        BoardCategory category = boardCategoryRepository.findById(boardType)
                .orElse(BoardCategory.builder()
                        .boardType(boardType)
                        .build());

        category.setName(name);
        category.setWritePermissionAdminOnly(writePermissionAdminOnly);

        return boardCategoryRepository.save(category);
    }
}
