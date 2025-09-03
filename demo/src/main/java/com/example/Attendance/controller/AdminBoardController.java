package com.example.Attendance.controller;

import com.example.Attendance.dto.BoardDTO;
import com.example.Attendance.dto.BoardUpdateDTO;
import com.example.Attendance.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/adminboard")
public class AdminBoardController {

    private final BoardService boardService;

    public AdminBoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @PostMapping("/write")
    public ResponseEntity<Map<String, String>> write(@RequestBody BoardDTO dto) {
        boardService.write(dto);
        Map<String, String> result = new HashMap<>();
        result.put("message", "글이 등록되었습니다.");
        return ResponseEntity.ok(result);
    }

    @GetMapping("/list/byType")
    public ResponseEntity<Map<String, Object>> getListByType(@RequestParam String type, @RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("id").descending());
        Page<BoardDTO> result = boardService.getListByType(type, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("list", result.getContent());
        response.put("totalPage", result.getTotalPages());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<?> detail(@PathVariable Long id) {
        BoardDTO board = boardService.getDetail(id);
        return ResponseEntity.ok(board);
    }

    /* 게시글 수정 */
    @PutMapping("/edit/{id}/{type}")
    public ResponseEntity<?> editBoard(
            @PathVariable Long id,
            @PathVariable String type,
            @RequestBody @Valid BoardUpdateDTO dto) {

        boardService.updateBoard(id, dto);
        Map<String, String> body = Collections.singletonMap("message", "게시글이 수정되었습니다.");
        return ResponseEntity.ok(body);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        boardService.delete(id);
        Map<String, String> result = new HashMap<>();
        result.put("message", "삭제되었습니다.");
        return ResponseEntity.ok(result);
    }

    // ★ 배치 삭제 추가 ★
    @DeleteMapping("/delete/batch")
    public ResponseEntity<?> deleteBatch(@RequestParam("ids") String ids) {
        List<Long> idList = Arrays.stream(ids.split(","))
                                  .map(Long::parseLong)
                                  .collect(Collectors.toList());

        boardService.deleteBatch(idList);

        Map<String, String> result = new HashMap<>();
        result.put("message", "선택한 게시글이 삭제되었습니다.");
        return ResponseEntity.ok(result);
    }

    @PostMapping("/recommend/{id}")
    public ResponseEntity<Map<String, Object>> recommend(@PathVariable Long id) {
        int count = boardService.recommend(id);
        Map<String, Object> body = new HashMap<>();
        body.put("count", count);
        return ResponseEntity.ok(body);
    }
}
