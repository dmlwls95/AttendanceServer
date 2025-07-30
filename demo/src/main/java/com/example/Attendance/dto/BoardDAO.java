package com.example.Attendance.dto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.Attendance.entity.Board;

@Repository
public class BoardDAO {

    @Autowired
    private DataSource dataSource;
 // 단일 게시글 조회
    public Board selectById(int id) {
        String sql = "SELECT * FROM board WHERE id = ?";
        try (
            Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
        ) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Board board = new Board();
                    board.setId(rs.getInt("id"));
                    board.setTitle(rs.getString("title"));
                    board.setContent(rs.getString("content"));
                    board.setWriter(rs.getString("writer"));
                    board.setWriteDate(rs.getDate("write_date")); // DB 컬럼명에 맞게 조정
                    return board;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    // 글 등록
    public void insert(Board dto) {
        String sql = "INSERT INTO board (id, title, content, writer) VALUES (board_seq.NEXTVAL, ?, ?, ?)";
        try (
            Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
        ) {
            ps.setString(1, dto.getTitle());
            ps.setString(2, dto.getContent());
            ps.setString(3, dto.getWriter());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 페이징된 게시글 목록 조회
    public List<Board> selectPage(int startRow, int endRow) {
        List<Board> list = new ArrayList<>();
        String sql = 
            "SELECT * FROM ( " +
            "  SELECT ROWNUM rnum, a.* FROM ( " +
            "    SELECT * FROM board ORDER BY id DESC " +
            "  ) a WHERE ROWNUM <= ? " +
            ") WHERE rnum >= ?";

        try (
            Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
        ) {
            ps.setInt(1, endRow);
            ps.setInt(2, startRow);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Board dto = new Board();
                    dto.setId(rs.getInt("id"));
                    dto.setTitle(rs.getString("title"));
                    dto.setContent(rs.getString("content"));
                    dto.setWriter(rs.getString("writer"));
                    dto.setWriteDate(rs.getDate("write_date"));
                    list.add(dto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // 전체 글 개수
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM board";
        try (
            Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
        ) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 글 삭제
    public void delete(int id) {
        String sql = "DELETE FROM board WHERE id = ?";
        try (
            Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
        ) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 
