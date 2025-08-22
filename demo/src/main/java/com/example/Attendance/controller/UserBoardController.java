package com.example.Attendance.controller;

import com.example.Attendance.dto.BoardDTO;
import com.example.Attendance.dto.BoardUpdateDTO;
import com.example.Attendance.dto.NormalResponse;
import com.example.Attendance.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;


@RestController
@RequestMapping("/user/userboard")
public class UserBoardController {

    private final BoardService boardService;
    
    public UserBoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @PostMapping("/write")
    public NormalResponse write(@RequestBody BoardDTO dto) {
    	
    	// normalresponse = new NormalResponse();
    	
        // 공지사항은 일반 사용자가 작성 못하게 막고 싶은 경우
        if ("NOTICE".equals(dto.getBoardType())) {
            
        	//NormalResponse normalresponse = new NormalResponse();
            //normalresponse.setSuccess(false);
            //normalresponse.setMessage("공지사항은 관리자만 쓸 수 있습니다");
        	return NormalResponse.builder()
        			.success(false)
        			.message("공지사항은 관리자만 쓸 수 있습니다")
        			.build();
        }

        // 정상 등록
        boardService.write(dto);
                
    	return NormalResponse.builder()
    			.success(true)
    			.message("등록성공")
    			.build();
    }
    
    @GetMapping("/list/byType")
    public ResponseEntity<Map<String,Object>> getListByType(@RequestParam String type, @RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("id").descending());
        Page<BoardDTO> result = boardService.getListByType(type, pageable);

        Map<String, Object> response = new HashMap<String, Object>();
        response.put("list", result.getContent());
        response.put("totalPage", result.getTotalPages());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/detail/{id}")
    public BoardDTO detail(@PathVariable Long id) {
        BoardDTO board = boardService.getDetail(id);
        return board;
    }
    /* 게시글 수정 */
    @PutMapping("/edit/{id}/{type}")
    public NormalResponse editBoard(
            @PathVariable Long id,
            @PathVariable String type,    // 필요 없다면 제거 가능
            @RequestBody @Valid BoardUpdateDTO dto) {

        boardService.updateBoard(id, dto);
        
        return NormalResponse.builder()
        		.success(true)
        		.message("게시글 수정성공")
        		.build();
        }
    
    @DeleteMapping("/delete/{id}")
    public NormalResponse delete(@PathVariable Long id) {
        boardService.delete(id);
        return NormalResponse.builder()
        		.success(true)
        		.message("삭제 성공")
        		.build();
    }
    
    @PostMapping("/recommend/{id}")
    public ResponseEntity<Map<String,Integer>> recommend(@PathVariable Long id) {
    	
        int count = boardService.recommend(id);
        Map<String,Integer> body = new HashMap<>();
        body.put("count", count);
        return ResponseEntity.ok(body);
    }
    
    @GetMapping("/recenttop")
    public List<BoardDTO> getRecentTopBoard()
    {
    	return boardService.getTop10Board();
    }
}

