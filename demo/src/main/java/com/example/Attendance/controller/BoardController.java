package com.example.Attendance.controller;

import com.example.Attendance.entity.Board;
import com.example.Attendance.repository.BoardRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/admin/board")
public class BoardController {

    @Autowired
    private BoardRepository boardRepository;

    // 📄 게시글 목록 (페이징)
    @GetMapping("/list")
    public Map<String, Object> list(@RequestParam(defaultValue = "1") int page) {
        int pageSize = 10;
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("id").descending());

        Page<Board> boardPage = boardRepository.findAll(pageable);

        Map<String, Object> result = new HashMap<>();
        result.put("list", boardPage.getContent());
        result.put("currentPage", page);
        result.put("totalPage", boardPage.getTotalPages());

        return result;
    }
 // 📄 게시글 상세 보기
    @GetMapping("/detail")
    public ResponseEntity<Board> detail(@RequestParam Long id) {
        Optional<Board> boardOpt = boardRepository.findById(id);
        return boardOpt.map(ResponseEntity::ok)
                       .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 📝 게시글 작성
    @PostMapping("/write")
    public Map<String, String> write(@RequestBody Board dto) {
        dto.setWriteDate(new Date());
        boardRepository.save(dto);
        return Collections.singletonMap("message", "글이 등록되었습니다.");
    }

    // ❌ 게시글 삭제
    @DeleteMapping("/delete/{id}")
    public Map<String, String> delete(@PathVariable Long id) {
        boardRepository.deleteById(id);
        return Collections.singletonMap("message", "글이 삭제되었습니다.");
    }
}