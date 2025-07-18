package com.example.Attendance.controller;

import com.example.Attendance.dto.BoardDAO;
import com.example.Attendance.dto.BoardDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/board")
public class BoardController {

    @Autowired
    private BoardDAO boardDAO;

    // 📄 게시글 목록 (페이징 포함)
    @GetMapping("/list")
    public Map<String, Object> list(@RequestParam(defaultValue = "1") int page) {
        int pageSize = 10;
        int startRow = (page - 1) * pageSize + 1;
        int endRow = page * pageSize;

        List<BoardDTO> list = boardDAO.selectPage(startRow, endRow);
        int totalCount = boardDAO.countAll();
        int totalPage = (int) Math.ceil(totalCount / (double) pageSize);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("currentPage", page);
        result.put("totalPage", totalPage);
        return result;
    }

    // 📝 게시글 작성
    @PostMapping("/write")
    public Map<String, String> write(@RequestBody BoardDTO dto) {
        boardDAO.insert(dto);
        Map<String, String> response = new HashMap<>();
        response.put("message", "글이 등록되었습니다.");
        return response;
    }

    // ❌ 게시글 삭제
    @DeleteMapping("/delete/{id}")
    public Map<String, String> delete(@PathVariable int id) {
        boardDAO.delete(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "글이 삭제되었습니다.");
        return response;
    }
}