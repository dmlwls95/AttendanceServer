package com.example.Attendance.controller;

import com.example.Attendance.entity.BoardCategory;
import com.example.Attendance.entity.BoardType;
import com.example.Attendance.service.BoardCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/boardcategory")
@RequiredArgsConstructor
public class BoardCategoryController {

    private final BoardCategoryService boardCategoryService;

    // 전체 게시판 리스트 (이름, 권한 포함)
    @GetMapping("/list")
    public ResponseEntity<List<BoardCategory>> list() {
        List<BoardCategory> categories = boardCategoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    // 특정 게시판 정보 조회
    @GetMapping("/{boardType}")
    public ResponseEntity<BoardCategory> getCategory(@PathVariable String boardType) {
        BoardType type = BoardType.valueOf(boardType.toUpperCase());
        BoardCategory category = boardCategoryService.getCategory(type);
        return ResponseEntity.ok(category);
    }

    // 게시판 이름 및 권한 수정
    @PutMapping("/{boardType}")
    public ResponseEntity<BoardCategory> updateCategory(
            @PathVariable String boardType,
            @RequestBody UpdateCategoryRequest request
    ) {
        BoardType type = BoardType.valueOf(boardType.toUpperCase());
        BoardCategory updated = boardCategoryService.updateCategory(type, request.getName(), request.isWritePermissionAdminOnly());
        return ResponseEntity.ok(updated);
    }

    public static class UpdateCategoryRequest {
        private String name;
        private boolean writePermissionAdminOnly;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public boolean isWritePermissionAdminOnly() { return writePermissionAdminOnly; }
        public void setWritePermissionAdminOnly(boolean writePermissionAdminOnly) { this.writePermissionAdminOnly = writePermissionAdminOnly; }
    }
}
