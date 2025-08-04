package com.example.Attendance.controller;

import com.example.Attendance.dto.BoardDTO;
import com.example.Attendance.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @PostMapping("/write")
    public ResponseEntity<?> write(@RequestBody BoardDTO dto) {
        boardService.write(dto);
        Map<String, String> result = new HashMap<String, String>();
        result.put("message", "글이 등록되었습니다.");
        return ResponseEntity.ok(result);
    }

    @GetMapping("/list/byType")
    public ResponseEntity<?> getListByType(@RequestParam String type, @RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("id").descending());
        Page<BoardDTO> result = boardService.getListByType(type, pageable);

        Map<String, Object> response = new HashMap<String, Object>();
        response.put("list", result.getContent());
        response.put("totalPage", result.getTotalPages());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<?> detail(@PathVariable Long id) {
        BoardDTO board = boardService.getDetail(id);
        return ResponseEntity.ok(board);
    }
    
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        boardService.delete(id);
        Map<String, String> result = new HashMap<String, String>();
        result.put("message", "삭제되었습니다.");
        return ResponseEntity.ok(result);
    }
}

