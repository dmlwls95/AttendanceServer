/* ===== 기초 마스터 ===== */
-- 직급 (ranks)  [id: rankid, seq: RANK_SEQ]
INSERT INTO ranks (rankid, rankname) VALUES (RANK_SEQ.NEXTVAL, '사원');
INSERT INTO ranks (rankid, rankname) VALUES (RANK_SEQ.NEXTVAL, '주임');
INSERT INTO ranks (rankid, rankname) VALUES (RANK_SEQ.NEXTVAL, '대리');
INSERT INTO ranks (rankid, rankname) VALUES (RANK_SEQ.NEXTVAL, '과장');
INSERT INTO ranks (rankid, rankname) VALUES (RANK_SEQ.NEXTVAL, '차장');
INSERT INTO ranks (rankid, rankname) VALUES (RANK_SEQ.NEXTVAL, '부장');

-- 부서 (DEPARTMENTS) [id: deptid, seq: DEPARTMENT_SEQ]
INSERT INTO DEPARTMENTS (deptid, deptname) VALUES (DEPARTMENT_SEQ.NEXTVAL, '인사팀');
INSERT INTO DEPARTMENTS (deptid, deptname) VALUES (DEPARTMENT_SEQ.NEXTVAL, '개발팀');
INSERT INTO DEPARTMENTS (deptid, deptname) VALUES (DEPARTMENT_SEQ.NEXTVAL, '영업팀');
INSERT INTO DEPARTMENTS (deptid, deptname) VALUES (DEPARTMENT_SEQ.NEXTVAL, '기획팀');
INSERT INTO DEPARTMENTS (deptid, deptname) VALUES (DEPARTMENT_SEQ.NEXTVAL, '총무팀');

-- 근무유형 (WORKTYPES) [id: worktypeid, seq: WORKTYPE_SEQ]
INSERT INTO WORKTYPES (worktypeid, worktypename) VALUES (WORKTYPE_SEQ.NEXTVAL, '정규직');
INSERT INTO WORKTYPES (worktypeid, worktypename) VALUES (WORKTYPE_SEQ.NEXTVAL, '계약직');
INSERT INTO WORKTYPES (worktypeid, worktypename) VALUES (WORKTYPE_SEQ.NEXTVAL, '프리랜서');
INSERT INTO WORKTYPES (worktypeid, worktypename) VALUES (WORKTYPE_SEQ.NEXTVAL, '인턴');

-- 예: 기존
-- INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at)
-- VALUES (ATTENDANCE_EVENTS_SEQ.NEXTVAL, (SELECT id FROM users WHERE email='user@aaa.com'), 'CLOCK_IN', TIMESTAMP '2025-08-11 09:00:00');


/* ===== 사용자 ===== */
-- 비밀번호 해시는 네가 주신 값 재사용
-- admin
INSERT INTO users (id, empnum, work_name, email, password, role, rankid, worktypeid, deptid, profileimageurl, work_start_time, work_end_time)
VALUES (USER_SEQ.NEXTVAL, 'a00000000', '관리자', 'admin@admin.com',
        '$2a$10$uqEQ/3hmmkdOJgrfRQz8seCCzPpWUwJijt2nD78/tcv5VTTqnif4m', 'ADMIN',
        (SELECT rankid FROM ranks WHERE rankname='부장'),
        (SELECT worktypeid FROM WORKTYPES WHERE worktypename='정규직'),
        (SELECT deptid FROM DEPARTMENTS WHERE deptname='총무팀'),
        'https://img.daisyui.com/images/profile/demo/yellingcat@192.webp', '09:00', '18:00');

-- tester
INSERT INTO users (id, empnum, work_name, email, password, role, rankid, worktypeid, deptid, profileimageurl, work_start_time, work_end_time)
VALUES (USER_SEQ.NEXTVAL, 'a123456789', '테스터', 'user@aaa.com',
        '$2a$10$i4RvLbeZk/bxEFA8NXk9YunSIwJi5mVwhsHQ.qtT7gakz.Xvmj5/W', 'USER',
        (SELECT rankid FROM ranks WHERE rankname='사원'),
        (SELECT worktypeid FROM WORKTYPES WHERE worktypename='정규직'),
        (SELECT deptid FROM DEPARTMENTS WHERE deptname='인사팀'),
        'https://img.daisyui.com/images/profile/demo/yellingcat@192.webp', '09:00', '18:00');

-- 추가 샘플 유저
INSERT INTO users (id, empnum, work_name, email, password, role, rankid, worktypeid, deptid, profileimageurl, work_start_time, work_end_time)
VALUES (USER_SEQ.NEXTVAL, 'a100000001', '김개발', 'dev1@corp.com',
        '$2a$10$uqEQ/3hmmkdOJgrfRQz8seCCzPpWUwJijt2nD78/tcv5VTTqnif4m', 'USER',
        (SELECT rankid FROM ranks WHERE rankname='대리'),
        (SELECT worktypeid FROM WORKTYPES WHERE worktypename='정규직'),
        (SELECT deptid FROM DEPARTMENTS WHERE deptname='개발팀'),
        'https://img.daisyui.com/images/profile/demo/yellingcat@192.webp', '10:00', '19:00');

INSERT INTO users (id, empnum, work_name, email, password, role, rankid, worktypeid, deptid, profileimageurl, work_start_time, work_end_time)
VALUES (USER_SEQ.NEXTVAL, 'a100000002', '이프론트', 'fe1@corp.com',
        '$2a$10$uqEQ/3hmmkdOJgrfRQz8seCCzPpWUwJijt2nD78/tcv5VTTqnif4m', 'USER',
        (SELECT rankid FROM ranks WHERE rankname='주임'),
        (SELECT worktypeid FROM WORKTYPES WHERE worktypename='정규직'),
        (SELECT deptid FROM DEPARTMENTS WHERE deptname='개발팀'),
        'https://img.daisyui.com/images/profile/demo/yellingcat@192.webp', '09:30', '18:30');

/* ===== 근태 (attendance) =====
   - 컬럼: user_id, work_date, clock_in, clock_out, is_late, is_left_early, total_hours(Double), overtime_minutes(int)
   - id는 ATTENDANCE_SEQ.NEXTVAL 사용
*/
-- tester (09:00~18:00)
INSERT INTO attendance (id, user_id, work_date, clock_in, clock_out, is_late, is_left_early, total_hours, overtime_minutes)
VALUES (ATTENDANCE_SEQ.NEXTVAL, (SELECT id FROM users WHERE email='user@aaa.com'),
        DATE '2025-08-12', TIMESTAMP '2025-08-12 09:03:00', TIMESTAMP '2025-08-12 18:30:00', 1, 0, 8.95, 30);

INSERT INTO attendance (id, user_id, work_date, clock_in, clock_out, is_late, is_left_early, total_hours, overtime_minutes)
VALUES (ATTENDANCE_SEQ.NEXTVAL, (SELECT id FROM users WHERE email='user@aaa.com'),
        DATE '2025-08-13', TIMESTAMP '2025-08-13 08:55:00', TIMESTAMP '2025-08-13 18:00:00', 0, 0, 9.00, 0);

INSERT INTO attendance (id, user_id, work_date, clock_in, clock_out, is_late, is_left_early, total_hours, overtime_minutes)
VALUES (ATTENDANCE_SEQ.NEXTVAL, (SELECT id FROM users WHERE email='user@aaa.com'),
        DATE '2025-08-14', TIMESTAMP '2025-08-14 09:12:00', TIMESTAMP '2025-08-14 19:20:00', 1, 0, 8.80, 80);

-- dev1 (10:00~19:00)
INSERT INTO attendance (id, user_id, work_date, clock_in, clock_out, is_late, is_left_early, total_hours, overtime_minutes)
VALUES (ATTENDANCE_SEQ.NEXTVAL, (SELECT id FROM users WHERE email='dev1@corp.com'),
        DATE '2025-08-12', TIMESTAMP '2025-08-12 10:01:00', TIMESTAMP '2025-08-12 19:45:00', 1, 0, 8.98, 45);

INSERT INTO attendance (id, user_id, work_date, clock_in, clock_out, is_late, is_left_early, total_hours, overtime_minutes)
VALUES (ATTENDANCE_SEQ.NEXTVAL, (SELECT id FROM users WHERE email='dev1@corp.com'),
        DATE '2025-08-13', TIMESTAMP '2025-08-13 10:00:00', TIMESTAMP '2025-08-13 19:00:00', 0, 0, 9.00, 0);

/* ============================================
   ATTENDANCE 샘플 추가
   - user@aaa.com(09:00~18:00), dev1(10:00~19:00),
     fe1(09:30~18:30), hr1(09:00~18:00),
     sales1(08:30~17:30), be1(09:00~18:00), plan1(09:00~18:00)
   - total_hours는 근무 경계 내(clamp) 기준, overtime_minutes는 초과분만
============================================ */

/* user@aaa.com */
INSERT INTO attendance (id, user_id, work_date, clock_in, clock_out, is_late, is_left_early, total_hours, overtime_minutes)
VALUES (ATTENDANCE_SEQ.NEXTVAL, (SELECT id FROM users WHERE email='user@aaa.com'),
        DATE '2025-08-11', TIMESTAMP '2025-08-11 09:00:00', TIMESTAMP '2025-08-11 18:05:00', 0, 0, 9.00, 5);

INSERT INTO attendance (id, user_id, work_date, clock_in, clock_out, is_late, is_left_early, total_hours, overtime_minutes)
VALUES (ATTENDANCE_SEQ.NEXTVAL, (SELECT id FROM users WHERE email='user@aaa.com'),
        DATE '2025-08-15', TIMESTAMP '2025-08-15 09:20:00', TIMESTAMP '2025-08-15 18:00:00', 1, 0, 8.67, 0);

/* dev1@corp.com (개발자) 10:00~19:00 */
INSERT INTO attendance (id, user_id, work_date, clock_in, clock_out, is_late, is_left_early, total_hours, overtime_minutes)
VALUES (ATTENDANCE_SEQ.NEXTVAL, (SELECT id FROM users WHERE email='dev1@corp.com'),
        DATE '2025-08-11', TIMESTAMP '2025-08-11 09:50:00', TIMESTAMP '2025-08-11 19:10:00', 0, 0, 9.00, 10);

INSERT INTO attendance (id, user_id, work_date, clock_in, clock_out, is_late, is_left_early, total_hours, overtime_minutes)
VALUES (ATTENDANCE_SEQ.NEXTVAL, (SELECT id FROM users WHERE email='dev1@corp.com'),
        DATE '2025-08-14', TIMESTAMP '2025-08-14 10:05:00', TIMESTAMP '2025-08-14 19:30:00', 1, 0, 8.92, 30);

/* fe1@corp.com (프론트) 09:30~18:30 */
INSERT INTO attendance (id, user_id, work_date, clock_in, clock_out, is_late, is_left_early, total_hours, overtime_minutes)
VALUES (ATTENDANCE_SEQ.NEXTVAL, (SELECT id FROM users WHERE email='fe1@corp.com'),
        DATE '2025-08-12', TIMESTAMP '2025-08-12 09:40:00', TIMESTAMP '2025-08-12 18:45:00', 1, 0, 8.83, 15);

INSERT INTO attendance (id, user_id, work_date, clock_in, clock_out, is_late, is_left_early, total_hours, overtime_minutes)
VALUES (ATTENDANCE_SEQ.NEXTVAL, (SELECT id FROM users WHERE email='fe1@corp.com'),
        DATE '2025-08-13', TIMESTAMP '2025-08-13 09:25:00', TIMESTAMP '2025-08-13 18:00:00', 0, 1, 8.50, 0);

/* hr1@corp.com (HR) 09:00~18:00 */
INSERT INTO attendance (id, user_id, work_date, clock_in, clock_out, is_late, is_left_early, total_hours, overtime_minutes)
VALUES (ATTENDANCE_SEQ.NEXTVAL, (SELECT id FROM users WHERE email='hr1@corp.com'),
        DATE '2025-08-12', TIMESTAMP '2025-08-12 09:10:00', TIMESTAMP '2025-08-12 18:40:00', 1, 0, 8.83, 40);

INSERT INTO attendance (id, user_id, work_date, clock_in, clock_out, is_late, is_left_early, total_hours, overtime_minutes)
VALUES (ATTENDANCE_SEQ.NEXTVAL, (SELECT id FROM users WHERE email='hr1@corp.com'),
        DATE '2025-08-14', TIMESTAMP '2025-08-14 08:55:00', TIMESTAMP '2025-08-14 17:40:00', 0, 1, 8.67, 0);

/* sales1@corp.com (영업) 08:30~17:30 */
INSERT INTO attendance (id, user_id, work_date, clock_in, clock_out, is_late, is_left_early, total_hours, overtime_minutes)
VALUES (ATTENDANCE_SEQ.NEXTVAL, (SELECT id FROM users WHERE email='sales1@corp.com'),
        DATE '2025-08-11', TIMESTAMP '2025-08-11 08:35:00', TIMESTAMP '2025-08-11 17:45:00', 1, 0, 8.92, 15);

INSERT INTO attendance (id, user_id, work_date, clock_in, clock_out, is_late, is_left_early, total_hours, overtime_minutes)
VALUES (ATTENDANCE_SEQ.NEXTVAL, (SELECT id FROM users WHERE email='sales1@corp.com'),
        DATE '2025-08-12', TIMESTAMP '2025-08-12 08:25:00', TIMESTAMP '2025-08-12 17:20:00', 0, 1, 8.83, 0);

INSERT INTO attendance (id, user_id, work_date, clock_in, clock_out, is_late, is_left_early, total_hours, overtime_minutes)
VALUES (ATTENDANCE_SEQ.NEXTVAL, (SELECT id FROM users WHERE email='sales1@corp.com'),
        DATE '2025-08-13', TIMESTAMP '2025-08-13 08:45:00', TIMESTAMP '2025-08-13 19:00:00', 1, 0, 8.75, 90);

/* be1@corp.com (백엔드) 09:00~18:00 */
INSERT INTO attendance (id, user_id, work_date, clock_in, clock_out, is_late, is_left_early, total_hours, overtime_minutes)
VALUES (ATTENDANCE_SEQ.NEXTVAL, (SELECT id FROM users WHERE email='be1@corp.com'),
        DATE '2025-08-12', TIMESTAMP '2025-08-12 09:00:00', TIMESTAMP '2025-08-12 18:10:00', 0, 0, 9.00, 10);

/* plan1@corp.com (기획) 09:00~18:00 */
INSERT INTO attendance (id, user_id, work_date, clock_in, clock_out, is_late, is_left_early, total_hours, overtime_minutes)
VALUES (ATTENDANCE_SEQ.NEXTVAL, (SELECT id FROM users WHERE email='plan1@corp.com'),
        DATE '2025-08-11', TIMESTAMP '2025-08-11 09:05:00', TIMESTAMP '2025-08-11 18:00:00', 1, 0, 8.92, 0);
        
        
/* ===============================
   QA (qa1@corp.com) 10:00~19:00
=============================== */
INSERT INTO attendance (id, user_id, work_date, clock_in, clock_out, is_late, is_left_early, total_hours, overtime_minutes)
VALUES (ATTENDANCE_SEQ.NEXTVAL, (SELECT id FROM users WHERE email='qa1@corp.com'),
        DATE '2025-08-01', TIMESTAMP '2025-08-01 10:02:00', TIMESTAMP '2025-08-01 19:00:00', 1, 0, 9.00, 0);

INSERT INTO attendance (id, user_id, work_date, clock_in, clock_out, is_late, is_left_early, total_hours, overtime_minutes)
VALUES (ATTENDANCE_SEQ.NEXTVAL, (SELECT id FROM users WHERE email='qa1@corp.com'),
        DATE '2025-08-04', TIMESTAMP '2025-08-04 09:58:00', TIMESTAMP '2025-08-04 19:30:00', 0, 0, 9.00, 30);

INSERT INTO attendance (id, user_id, work_date, clock_in, clock_out, is_late, is_left_early, total_hours, overtime_minutes)
VALUES (ATTENDANCE_SEQ.NEXTVAL, (SELECT id FROM users WHERE email='qa1@corp.com'),
        DATE '2025-08-05', TIMESTAMP '2025-08-05 10:10:00', TIMESTAMP '2025-08-05 18:50:00', 1, 1, 8.67, 0);

INSERT INTO attendance (id, user_id, work_date, clock_in, clock_out, is_late, is_left_early, total_hours, overtime_minutes)
VALUES (ATTENDANCE_SEQ.NEXTVAL, (SELECT id FROM users WHERE email='qa1@corp.com'),
        DATE '2025-08-06', TIMESTAMP '2025-08-06 09:50:00', TIMESTAMP '2025-08-06 19:00:00', 0, 0, 9.00, 0);

/* ===============================
   INTERN (intern1@corp.com) 09:00~18:00
=============================== */
INSERT INTO attendance (id, user_id, work_date, clock_in, clock_out, is_late, is_left_early, total_hours, overtime_minutes)
VALUES (ATTENDANCE_SEQ.NEXTVAL, (SELECT id FROM users WHERE email='intern1@corp.com'),
        DATE '2025-08-06', TIMESTAMP '2025-08-06 09:00:00', TIMESTAMP '2025-08-06 18:02:00', 0, 0, 9.00, 2);

INSERT INTO attendance (id, user_id, work_date, clock_in, clock_out, is_late, is_left_early, total_hours, overtime_minutes)
VALUES (ATTENDANCE_SEQ.NEXTVAL, (SELECT id FROM users WHERE email='intern1@corp.com'),
        DATE '2025-08-07', TIMESTAMP '2025-08-07 09:12:00', TIMESTAMP '2025-08-07 17:55:00', 1, 1, 8.72, 0);

/* ===============================
   계약직 (contract1@corp.com) 10:00~19:00
=============================== */
INSERT INTO attendance (id, user_id, work_date, clock_in, clock_out, is_late, is_left_early, total_hours, overtime_minutes)
VALUES (ATTENDANCE_SEQ.NEXTVAL, (SELECT id FROM users WHERE email='contract1@corp.com'),
        DATE '2025-08-07', TIMESTAMP '2025-08-07 10:03:00', TIMESTAMP '2025-08-07 19:05:00', 1, 0, 9.00, 5);

INSERT INTO attendance (id, user_id, work_date, clock_in, clock_out, is_late, is_left_early, total_hours, overtime_minutes)
VALUES (ATTENDANCE_SEQ.NEXTVAL, (SELECT id FROM users WHERE email='contract1@corp.com'),
        DATE '2025-08-08', TIMESTAMP '2025-08-08 09:55:00', TIMESTAMP '2025-08-08 18:40:00', 0, 0, 9.00, 0);

/* ===============================
   프리랜서 (freelance1@corp.com) 13:00~22:00
=============================== */
INSERT INTO attendance (id, user_id, work_date, clock_in, clock_out, is_late, is_left_early, total_hours, overtime_minutes)
VALUES (ATTENDANCE_SEQ.NEXTVAL, (SELECT id FROM users WHERE email='freelance1@corp.com'),
        DATE '2025-08-01', TIMESTAMP '2025-08-01 13:05:00', TIMESTAMP '2025-08-01 22:10:00', 1, 0, 9.00, 10);

INSERT INTO attendance (id, user_id, work_date, clock_in, clock_out, is_late, is_left_early, total_hours, overtime_minutes)
VALUES (ATTENDANCE_SEQ.NEXTVAL, (SELECT id FROM users WHERE email='freelance1@corp.com'),
        DATE '2025-08-04', TIMESTAMP '2025-08-04 12:55:00', TIMESTAMP '2025-08-04 21:40:00', 0, 1, 8.67, 0);

/* ===============================
   프론트 (fe1@corp.com) 09:30~18:30  — 추가 날짜
=============================== */
INSERT INTO attendance (id, user_id, work_date, clock_in, clock_out, is_late, is_left_early, total_hours, overtime_minutes)
VALUES (ATTENDANCE_SEQ.NEXTVAL, (SELECT id FROM users WHERE email='fe1@corp.com'),
        DATE '2025-08-05', TIMESTAMP '2025-08-05 09:28:00', TIMESTAMP '2025-08-05 18:20:00', 0, 1, 8.83, 0);

INSERT INTO attendance (id, user_id, work_date, clock_in, clock_out, is_late, is_left_early, total_hours, overtime_minutes)
VALUES (ATTENDANCE_SEQ.NEXTVAL, (SELECT id FROM users WHERE email='fe1@corp.com'),
        DATE '2025-08-06', TIMESTAMP '2025-08-06 09:35:00', TIMESTAMP '2025-08-06 18:45:00', 1, 0, 9.00, 15);

/* ===============================
   백엔드 (be1@corp.com) 09:00~18:00 — 추가 날짜
=============================== */
INSERT INTO attendance (id, user_id, work_date, clock_in, clock_out, is_late, is_left_early, total_hours, overtime_minutes)
VALUES (ATTENDANCE_SEQ.NEXTVAL, (SELECT id FROM users WHERE email='be1@corp.com'),
        DATE '2025-08-05', TIMESTAMP '2025-08-05 09:05:00', TIMESTAMP '2025-08-05 18:40:00', 1, 0, 8.92, 40);

INSERT INTO attendance (id, user_id, work_date, clock_in, clock_out, is_late, is_left_early, total_hours, overtime_minutes)
VALUES (ATTENDANCE_SEQ.NEXTVAL, (SELECT id FROM users WHERE email='be1@corp.com'),
        DATE '2025-08-06', TIMESTAMP '2025-08-06 08:55:00', TIMESTAMP '2025-08-06 18:00:00', 0, 0, 9.00, 0);

/* ===============================
   영업 (sales1@corp.com) 08:30~17:30 — 추가 날짜
=============================== */
INSERT INTO attendance (id, user_id, work_date, clock_in, clock_out, is_late, is_left_early, total_hours, overtime_minutes)
VALUES (ATTENDANCE_SEQ.NEXTVAL, (SELECT id FROM users WHERE email='sales1@corp.com'),
        DATE '2025-08-04', TIMESTAMP '2025-08-04 08:35:00', TIMESTAMP '2025-08-04 17:45:00', 1, 0, 8.92, 15);

INSERT INTO attendance (id, user_id, work_date, clock_in, clock_out, is_late, is_left_early, total_hours, overtime_minutes)
VALUES (ATTENDANCE_SEQ.NEXTVAL, (SELECT id FROM users WHERE email='sales1@corp.com'),
        DATE '2025-08-05', TIMESTAMP '2025-08-05 08:25:00', TIMESTAMP '2025-08-05 17:20:00', 0, 1, 8.83, 0);

/* ===============================
   기획 (plan1@corp.com) 09:00~18:00 — 추가 날짜
=============================== */
INSERT INTO attendance (id, user_id, work_date, clock_in, clock_out, is_late, is_left_early, total_hours, overtime_minutes)
VALUES (ATTENDANCE_SEQ.NEXTVAL, (SELECT id FROM users WHERE email='plan1@corp.com'),
        DATE '2025-08-05', TIMESTAMP '2025-08-05 09:10:00', TIMESTAMP '2025-08-05 18:00:00', 1, 0, 8.83, 0);

/* ===== 게시판 (board, board_comment) =====
   Board 컬럼: id, recommendCount, title, content, writer, writeDate, boardType
   Comment 컬럼: id, content, createdAt, board_id
*/
-- 게시판 글 (RECOMMEND_COUNT 꼭 포함!)
INSERT INTO board (id, recommend_count, title, content, writer, write_date, board_type)
VALUES (BOARD_SEQ.NEXTVAL, 5,
        '근태 시스템 오픈 안내',
        '새로운 근태 관리 시스템이 오픈되었습니다. 문의는 인사팀으로 부탁드립니다.',
        '관리자',
        TIMESTAMP '2025-08-12 09:00:00',
        'NOTICE');

INSERT INTO board (id, recommend_count, title, content, writer, write_date, board_type)
VALUES (BOARD_SEQ.NEXTVAL, 2,
        '점심 추천 받습니다',
        '회사 근처 맛집 추천 부탁드려요 🍜',
        '이프론트',
        TIMESTAMP '2025-08-13 11:40:00',
        'FREE');

INSERT INTO board (id, recommend_count, title, content, writer, write_date, board_type)
VALUES (BOARD_SEQ.NEXTVAL, 3,
        'API 가이드 v1',
        '사내 API 규약과 공통 에러 포맷을 공유합니다.',
        '박백엔드',
        TIMESTAMP '2025-08-13 15:10:00',
        'SUGGEST');
        

-- =========================
-- BOARD 샘플 데이터 추가
-- 컬럼: id, recommend_count, title, content, writer, write_date, board_type
-- 시퀀스: BOARD_SEQ.NEXTVAL
-- =========================

INSERT INTO board (id, recommend_count, title, content, writer, write_date, board_type)
VALUES (BOARD_SEQ.NEXTVAL, 1,
        '근태 시스템 점검 안내 (8/20 00:00~02:00)',
        '8월 20일(수) 00:00~02:00 사이에 시스템 점검이 예정되어 있습니다. 점검 시간에는 서비스 이용이 제한됩니다.',
        '관리자',
        TIMESTAMP '2025-08-15 09:00:00',
        'NOTICE');

INSERT INTO board (id, recommend_count, title, content, writer, write_date, board_type)
VALUES (BOARD_SEQ.NEXTVAL, 0,
        '외근/재택 근무 기록 가이드',
        '외근 및 재택 근무 시 근태 기록 방법을 안내드립니다. 외근: 외근 사유 입력 필수, 재택: 계획 승인 후 기록.',
        '정HR',
        TIMESTAMP '2025-08-11 10:30:00',
        'NOTICE');

INSERT INTO board (id, recommend_count, title, content, writer, write_date, board_type)
VALUES (BOARD_SEQ.NEXTVAL, 4,
        'React 훅 모음 레퍼런스 공유',
        '프로젝트에서 자주 쓰는 커스텀 훅 정리본을 공유합니다. PR 환영합니다.',
        '이프론트',
        TIMESTAMP '2025-08-10 14:15:00',
        'SUGGEST');

INSERT INTO board (id, recommend_count, title, content, writer, write_date, board_type)
VALUES (BOARD_SEQ.NEXTVAL, 3,
        'Spring Boot 에러 핸들링 베스트 프랙티스',
        'ControllerAdvice, ErrorResponse 표준화 샘플과 함께 예외 매핑 전략을 정리했습니다.',
        '박백엔드',
        TIMESTAMP '2025-08-09 16:40:00',
        'SUGGEST');

INSERT INTO board (id, recommend_count, title, content, writer, write_date, board_type)
VALUES (BOARD_SEQ.NEXTVAL, 2,
        '점심에 짜장/짬뽕 투표합시다',
        '오늘 점심 메뉴 투표합니다. 댓글로 남겨주세요! (13:00 마감)',
        '김개발',
        TIMESTAMP '2025-08-14 11:20:00',
        'FREE');

INSERT INTO board (id, recommend_count, title, content, writer, write_date, board_type)
VALUES (BOARD_SEQ.NEXTVAL, 5,
        '사내 축구 동호회 8월 일정',
        '매주 수 19:00 구장 예약 완료. 참가자는 월요일까지 댓글로 이름 남겨주세요.',
        '오영업',
        TIMESTAMP '2025-08-08 09:35:00',
        'FREE');

INSERT INTO board (id, recommend_count, title, content, writer, write_date, board_type)
VALUES (BOARD_SEQ.NEXTVAL, 0,
        '코드 리뷰 체크리스트 초안',
        '가독성, 예외 처리, 테스트 커버리지 등 공통 체크리스트 문서를 초안으로 공유합니다.',
        '박백엔드',
        TIMESTAMP '2025-08-07 18:10:00',
        'SUGGEST');

INSERT INTO board (id, recommend_count, title, content, writer, write_date, board_type)
VALUES (BOARD_SEQ.NEXTVAL, 1,
        '신규 입사자 온보딩 일정(8월 3주차)',
        '3주차 온보딩 교육 일정 공유드립니다. 장비 수령 및 계정 발급 일정 포함.',
        '관리자',
        TIMESTAMP '2025-08-12 08:50:00',
        'NOTICE');

INSERT INTO board (id, recommend_count, title, content, writer, write_date, board_type)
VALUES (BOARD_SEQ.NEXTVAL, 6,
        'PostgreSQL에서 효율적인 인덱스 전략',
        'B-Tree/GIN/Hash 인덱스 비교와 실제 쿼리 플랜 사례 공유.',
        '박백엔드',
        TIMESTAMP '2025-08-05 17:25:00',
        'SUGGEST');

INSERT INTO board (id, recommend_count, title, content, writer, write_date, board_type)
VALUES (BOARD_SEQ.NEXTVAL, 2,
        '디자인 시스템 피그마 파일 업데이트',
        '버튼/폼 컴포넌트 토큰 변경 사항 반영했습니다. 확인 후 이의 있으면 코멘트 주세요.',
        '문기획',
        TIMESTAMP '2025-08-06 10:05:00',
        'FREE');

-- 더미 몇 개 추가
INSERT INTO board (id, recommend_count, title, content, writer, write_date, board_type)
VALUES (BOARD_SEQ.NEXTVAL, 0,
        '8월 보안 점검 체크포인트',
        '비밀번호 정책 및 2FA 점검 안내 드립니다.',
        '관리자',
        TIMESTAMP '2025-08-04 09:10:00',
        'NOTICE');

INSERT INTO board (id, recommend_count, title, content, writer, write_date, board_type)
VALUES (BOARD_SEQ.NEXTVAL, 3,
        '팀별 회고 템플릿 공유',
        '월간/분기 회고에 사용할 공통 템플릿입니다. 팀 특성에 맞게 수정해서 쓰세요.',
        '정HR',
        TIMESTAMP '2025-08-03 13:30:00',
        'SUGGEST');

-- 댓글
INSERT INTO board_comment (id, content, created_at, board_id)
VALUES (BOARD_COMMENT_SEQ.NEXTVAL, '수고하셨습니다! 👍',
        TIMESTAMP '2025-08-12 10:00:00',
        (SELECT id FROM board WHERE title='근태 시스템 오픈 안내'));

INSERT INTO board_comment (id, content, created_at, board_id)
VALUES (BOARD_COMMENT_SEQ.NEXTVAL,
        '회사 맞은편 우동집 맛있어요.',
        TIMESTAMP '2025-08-13 12:10:00',
        (SELECT id FROM board WHERE title='점심 추천 받습니다'));

INSERT INTO board_comment (id, content, created_at, board_id)
VALUES (BOARD_COMMENT_SEQ.NEXTVAL,
        '좋은 자료 감사합니다.',
        TIMESTAMP '2025-08-13 16:00:00',
        (SELECT id FROM board WHERE title='API 가이드 v1'));

/* =========================
   BOARD_COMMENT 더미 데이터
   컬럼: id, content, created_at, board_id
   FK: board_id → board.id
========================= */

-- 근태 시스템 오픈 안내
INSERT INTO board_comment (id, content, created_at, board_id)
VALUES (BOARD_COMMENT_SEQ.NEXTVAL, '수고하셨습니다! 👍', TIMESTAMP '2025-08-12 10:00:00',
        (SELECT id FROM board WHERE title='근태 시스템 오픈 안내'));

INSERT INTO board_comment (id, content, created_at, board_id)
VALUES (BOARD_COMMENT_SEQ.NEXTVAL, '도입 가이드 문서 링크 부탁드려요.', TIMESTAMP '2025-08-12 10:15:00',
        (SELECT id FROM board WHERE title='근태 시스템 오픈 안내'));

INSERT INTO board_comment (id, content, created_at, board_id)
VALUES (BOARD_COMMENT_SEQ.NEXTVAL, '모바일에서도 잘 동작합니다.', TIMESTAMP '2025-08-12 11:05:00',
        (SELECT id FROM board WHERE title='근태 시스템 오픈 안내'));

-- 점검 안내
INSERT INTO board_comment (id, content, created_at, board_id)
VALUES (BOARD_COMMENT_SEQ.NEXTVAL, '점검 시간 확인했습니다.', TIMESTAMP '2025-08-15 09:20:00',
        (SELECT id FROM board WHERE title='근태 시스템 점검 안내 (8/20 00:00~02:00)'));

INSERT INTO board_comment (id, content, created_at, board_id)
VALUES (BOARD_COMMENT_SEQ.NEXTVAL, '점검 후 캐시 삭제 필요할까요?', TIMESTAMP '2025-08-15 09:42:00',
        (SELECT id FROM board WHERE title='근태 시스템 점검 안내 (8/20 00:00~02:00)'));

-- 외근/재택 가이드
INSERT INTO board_comment (id, content, created_at, board_id)
VALUES (BOARD_COMMENT_SEQ.NEXTVAL, '재택 승인 프로세스 명확해서 좋네요.', TIMESTAMP '2025-08-11 11:05:00',
        (SELECT id FROM board WHERE title='외근/재택 근무 기록 가이드'));

-- 자유게시판: 점심 추천
INSERT INTO board_comment (id, content, created_at, board_id)
VALUES (BOARD_COMMENT_SEQ.NEXTVAL, '회사 맞은편 우동집 추천!', TIMESTAMP '2025-08-13 12:10:00',
        (SELECT id FROM board WHERE title='점심 추천 받습니다'));

INSERT INTO board_comment (id, content, created_at, board_id)
VALUES (BOARD_COMMENT_SEQ.NEXTVAL, '오늘은 비와서 라멘 어때요?', TIMESTAMP '2025-08-13 12:15:00',
        (SELECT id FROM board WHERE title='점심 추천 받습니다'));

-- 자유게시판: 축구 동호회
INSERT INTO board_comment (id, content, created_at, board_id)
VALUES (BOARD_COMMENT_SEQ.NEXTVAL, '저도 참여합니다! 이름 올립니다.', TIMESTAMP '2025-08-08 10:00:00',
        (SELECT id FROM board WHERE title='사내 축구 동호회 8월 일정'));

INSERT INTO board_comment (id, content, created_at, board_id)
VALUES (BOARD_COMMENT_SEQ.NEXTVAL, '골키퍼 가능해요 🙋', TIMESTAMP '2025-08-08 10:12:00',
        (SELECT id FROM board WHERE title='사내 축구 동호회 8월 일정'));

-- 개발 공유: React 훅 모음
INSERT INTO board_comment (id, content, created_at, board_id)
VALUES (BOARD_COMMENT_SEQ.NEXTVAL, 'useInterval 훅도 추가 부탁드려요.', TIMESTAMP '2025-08-10 15:00:00',
        (SELECT id FROM board WHERE title='React 훅 모음 레퍼런스 공유'));

INSERT INTO board_comment (id, content, created_at, board_id)
VALUES (BOARD_COMMENT_SEQ.NEXTVAL, 'PR 올렸습니다. 확인 부탁!', TIMESTAMP '2025-08-10 17:40:00',
        (SELECT id FROM board WHERE title='React 훅 모음 레퍼런스 공유'));

-- 개발 공유: Spring Boot 에러 핸들링
INSERT INTO board_comment (id, content, created_at, board_id)
VALUES (BOARD_COMMENT_SEQ.NEXTVAL, 'ErrorResponse 포맷 예시 깔끔하네요.', TIMESTAMP '2025-08-09 17:05:00',
        (SELECT id FROM board WHERE title='Spring Boot 에러 핸들링 베스트 프랙티스'));

-- 제안: 코드 리뷰 체크리스트
INSERT INTO board_comment (id, content, created_at, board_id)
VALUES (BOARD_COMMENT_SEQ.NEXTVAL, '동일 파일 변경 라인 수 기준도 넣으면 좋겠어요.', TIMESTAMP '2025-08-07 09:20:00',
        (SELECT id FROM board WHERE title='코드 리뷰 체크리스트 초안'));

-- FREE: 디자인 시스템 업데이트
INSERT INTO board_comment (id, content, created_at, board_id)
VALUES (BOARD_COMMENT_SEQ.NEXTVAL, '토큰 변경 내역 노션에도 반영해주세요.', TIMESTAMP '2025-08-06 11:00:00',
        (SELECT id FROM board WHERE title='디자인 시스템 피그마 파일 업데이트'));



/* =========================================
   attendance_events 더미 (ATTENDANCE에 맞춰 생성)
   컬럼: id, user_id, event_type, occurred_at
   시퀀스: attendance_events_SEQ.NEXTVAL
========================================= */

-- ATTENDANCE_EVENTS.SOURCE 컬럼의 기본값만 설정 (NOT NULL은 이미 있으므로 제외)
ALTER TABLE ATTENDANCE_EVENTS
  MODIFY (SOURCE DEFAULT 'WEB');

/* ===========================
   ATTENDANCE_EVENTS (safe upsert style)
   - INSERT ... SELECT (유저 없으면 0행 삽입, 에러 없음)
   - 반드시 users INSERT 후 실행 권장
=========================== */

/* user@aaa.com (09:00~18:00) */
INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_IN',  TIMESTAMP '2025-08-11 09:00:00', 'WEB' FROM users u WHERE u.email='user@aaa.com';
INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_OUT', TIMESTAMP '2025-08-11 18:05:00', 'WEB' FROM users u WHERE u.email='user@aaa.com';

INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_IN',  TIMESTAMP '2025-08-12 09:03:00', 'WEB' FROM users u WHERE u.email='user@aaa.com';
INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_OUT', TIMESTAMP '2025-08-12 18:30:00', 'WEB' FROM users u WHERE u.email='user@aaa.com';

INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_IN',  TIMESTAMP '2025-08-13 08:55:00', 'WEB' FROM users u WHERE u.email='user@aaa.com';
INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_OUT', TIMESTAMP '2025-08-13 18:00:00', 'WEB' FROM users u WHERE u.email='user@aaa.com';

INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_IN',  TIMESTAMP '2025-08-14 09:12:00', 'WEB' FROM users u WHERE u.email='user@aaa.com';
INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_OUT', TIMESTAMP '2025-08-14 19:20:00', 'WEB' FROM users u WHERE u.email='user@aaa.com';

INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_IN',  TIMESTAMP '2025-08-15 09:20:00', 'WEB' FROM users u WHERE u.email='user@aaa.com';
INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_OUT', TIMESTAMP '2025-08-15 18:00:00', 'WEB' FROM users u WHERE u.email='user@aaa.com';

/* dev1@corp.com (10:00~19:00) */
INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_IN',  TIMESTAMP '2025-08-11 09:50:00', 'WEB' FROM users u WHERE u.email='dev1@corp.com';
INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_OUT', TIMESTAMP '2025-08-11 19:10:00', 'WEB' FROM users u WHERE u.email='dev1@corp.com';

INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_IN',  TIMESTAMP '2025-08-12 10:01:00', 'WEB' FROM users u WHERE u.email='dev1@corp.com';
INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_OUT', TIMESTAMP '2025-08-12 19:45:00', 'WEB' FROM users u WHERE u.email='dev1@corp.com';

INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_IN',  TIMESTAMP '2025-08-13 10:00:00', 'WEB' FROM users u WHERE u.email='dev1@corp.com';
INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_OUT', TIMESTAMP '2025-08-13 19:00:00', 'WEB' FROM users u WHERE u.email='dev1@corp.com';

INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_IN',  TIMESTAMP '2025-08-14 10:05:00', 'WEB' FROM users u WHERE u.email='dev1@corp.com';
INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_OUT', TIMESTAMP '2025-08-14 19:30:00', 'WEB' FROM users u WHERE u.email='dev1@corp.com';

/* fe1@corp.com (09:30~18:30) */
INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_IN',  TIMESTAMP '2025-08-05 09:28:00', 'WEB' FROM users u WHERE u.email='fe1@corp.com';
INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_OUT', TIMESTAMP '2025-08-05 18:20:00', 'WEB' FROM users u WHERE u.email='fe1@corp.com';

INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_IN',  TIMESTAMP '2025-08-06 09:35:00', 'WEB' FROM users u WHERE u.email='fe1@corp.com';
INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_OUT', TIMESTAMP '2025-08-06 18:45:00', 'WEB' FROM users u WHERE u.email='fe1@corp.com';

INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_IN',  TIMESTAMP '2025-08-12 09:40:00', 'WEB' FROM users u WHERE u.email='fe1@corp.com';
INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_OUT', TIMESTAMP '2025-08-12 18:45:00', 'WEB' FROM users u WHERE u.email='fe1@corp.com';

INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_IN',  TIMESTAMP '2025-08-13 09:25:00', 'WEB' FROM users u WHERE u.email='fe1@corp.com';
INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_OUT', TIMESTAMP '2025-08-13 18:00:00', 'WEB' FROM users u WHERE u.email='fe1@corp.com';

-- 퇴근 미기록일: CLOCK_IN만
INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_IN',  TIMESTAMP '2025-08-14 09:34:00', 'WEB' FROM users u WHERE u.email='fe1@corp.com';

/* hr1@corp.com (09:00~18:00) */
INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_IN',  TIMESTAMP '2025-08-12 09:10:00', 'WEB' FROM users u WHERE u.email='hr1@corp.com';
INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_OUT', TIMESTAMP '2025-08-12 18:40:00', 'WEB' FROM users u WHERE u.email='hr1@corp.com';

/* ===== 추가로 이전에 만들었던 나머지 사용자들도 동일 패턴으로 ===== */

/* sales1@corp.com (08:30~17:30) */
INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_IN',  TIMESTAMP '2025-08-04 08:35:00', 'WEB' FROM users u WHERE u.email='sales1@corp.com';
INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_OUT', TIMESTAMP '2025-08-04 17:45:00', 'WEB' FROM users u WHERE u.email='sales1@corp.com';

INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_IN',  TIMESTAMP '2025-08-05 08:25:00', 'WEB' FROM users u WHERE u.email='sales1@corp.com';
INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_OUT', TIMESTAMP '2025-08-05 17:20:00', 'WEB' FROM users u WHERE u.email='sales1@corp.com';

INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_IN',  TIMESTAMP '2025-08-11 08:35:00', 'WEB' FROM users u WHERE u.email='sales1@corp.com';
INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_OUT', TIMESTAMP '2025-08-11 17:45:00', 'WEB' FROM users u WHERE u.email='sales1@corp.com';

INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_IN',  TIMESTAMP '2025-08-12 08:25:00', 'WEB' FROM users u WHERE u.email='sales1@corp.com';
INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_OUT', TIMESTAMP '2025-08-12 17:20:00', 'WEB' FROM users u WHERE u.email='sales1@corp.com';

INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_IN',  TIMESTAMP '2025-08-13 08:45:00', 'WEB' FROM users u WHERE u.email='sales1@corp.com';
INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_OUT', TIMESTAMP '2025-08-13 19:00:00', 'WEB' FROM users u WHERE u.email='sales1@corp.com';

/* be1@corp.com (09:00~18:00) */
INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_IN',  TIMESTAMP '2025-08-05 09:05:00', 'WEB' FROM users u WHERE u.email='be1@corp.com';
INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_OUT', TIMESTAMP '2025-08-05 18:40:00', 'WEB' FROM users u WHERE u.email='be1@corp.com';

INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_IN',  TIMESTAMP '2025-08-06 08:55:00', 'WEB' FROM users u WHERE u.email='be1@corp.com';
INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_OUT', TIMESTAMP '2025-08-06 18:00:00', 'WEB' FROM users u WHERE u.email='be1@corp.com';

INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_IN',  TIMESTAMP '2025-08-12 09:00:00', 'WEB' FROM users u WHERE u.email='be1@corp.com';
INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_OUT', TIMESTAMP '2025-08-12 18:10:00', 'WEB' FROM users u WHERE u.email='be1@corp.com';

/* plan1@corp.com (09:00~18:00) */
INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_IN',  TIMESTAMP '2025-08-11 09:05:00', 'WEB' FROM users u WHERE u.email='plan1@corp.com';
INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_OUT', TIMESTAMP '2025-08-11 18:00:00', 'WEB' FROM users u WHERE u.email='plan1@corp.com';

INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_IN',  TIMESTAMP '2025-08-05 09:10:00', 'WEB' FROM users u WHERE u.email='plan1@corp.com';
INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_OUT', TIMESTAMP '2025-08-05 18:00:00', 'WEB' FROM users u WHERE u.email='plan1@corp.com';

/* qa1@corp.com (10:00~19:00) */
INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_IN',  TIMESTAMP '2025-08-01 10:02:00', 'WEB' FROM users u WHERE u.email='qa1@corp.com';
INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_OUT', TIMESTAMP '2025-08-01 19:00:00', 'WEB' FROM users u WHERE u.email='qa1@corp.com';

INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_IN',  TIMESTAMP '2025-08-04 09:58:00', 'WEB' FROM users u WHERE u.email='qa1@corp.com';
INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_OUT', TIMESTAMP '2025-08-04 19:30:00', 'WEB' FROM users u WHERE u.email='qa1@corp.com';

INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_IN',  TIMESTAMP '2025-08-05 10:10:00', 'WEB' FROM users u WHERE u.email='qa1@corp.com';
INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_OUT', TIMESTAMP '2025-08-05 18:50:00', 'WEB' FROM users u WHERE u.email='qa1@corp.com';

INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_IN',  TIMESTAMP '2025-08-06 09:50:00', 'WEB' FROM users u WHERE u.email='qa1@corp.com';
INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_OUT', TIMESTAMP '2025-08-06 19:00:00', 'WEB' FROM users u WHERE u.email='qa1@corp.com';

/* intern1@corp.com (09:00~18:00) */
INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_IN',  TIMESTAMP '2025-08-06 09:00:00', 'WEB' FROM users u WHERE u.email='intern1@corp.com';
INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_OUT', TIMESTAMP '2025-08-06 18:02:00', 'WEB' FROM users u WHERE u.email='intern1@corp.com';

INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_IN',  TIMESTAMP '2025-08-07 09:12:00', 'WEB' FROM users u WHERE u.email='intern1@corp.com';
INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_OUT', TIMESTAMP '2025-08-07 17:55:00', 'WEB' FROM users u WHERE u.email='intern1@corp.com';

/* contract1@corp.com (10:00~19:00) */
INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_IN',  TIMESTAMP '2025-08-07 10:03:00', 'WEB' FROM users u WHERE u.email='contract1@corp.com';
INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_OUT', TIMESTAMP '2025-08-07 19:05:00', 'WEB' FROM users u WHERE u.email='contract1@corp.com';

INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_IN',  TIMESTAMP '2025-08-08 09:55:00', 'WEB' FROM users u WHERE u.email='contract1@corp.com';
INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_OUT', TIMESTAMP '2025-08-08 18:40:00', 'WEB' FROM users u WHERE u.email='contract1@corp.com';

/* freelance1@corp.com (13:00~22:00) */
INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_IN',  TIMESTAMP '2025-08-01 13:05:00', 'WEB' FROM users u WHERE u.email='freelance1@corp.com';
INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_OUT', TIMESTAMP '2025-08-01 22:10:00', 'WEB' FROM users u WHERE u.email='freelance1@corp.com';

INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_IN',  TIMESTAMP '2025-08-04 12:55:00', 'WEB' FROM users u WHERE u.email='freelance1@corp.com';
INSERT INTO ATTENDANCE_EVENTS (id, user_id, event_type, occurred_at, source)
SELECT ATTENDANCE_EVENTS_SEQ.NEXTVAL, u.id, 'CLOCK_OUT', TIMESTAMP '2025-08-04 21:40:00', 'WEB' FROM users u WHERE u.email='freelance1@corp.com';

-- 연차(오늘 포함되도록) : user@aaa.com / 2025-08-13 ~ 2025-08-14
INSERT INTO LEAVE (leave_id, leave_type, start_date, end_date, reason, status, user_id)
SELECT LEAVE_SEQ.NEXTVAL, 'Annual', DATE '2025-08-13', DATE '2025-08-14', '여름 휴가', 'APPROVED', u.id
FROM USERS u WHERE u.email = 'user@aaa.com';

-- 병가 : user@aaa.com / 2025-08-10 ~ 2025-08-11
INSERT INTO LEAVE (leave_id, leave_type, start_date, end_date, reason, status, user_id)
SELECT LEAVE_SEQ.NEXTVAL, 'Sick', DATE '2025-08-10', DATE '2025-08-11', '감기 몸살', 'APPROVED', u.id
FROM USERS u WHERE u.email = 'user@aaa.com';

-- 휴직(미래 기간) : user@aaa.com / 2025-08-20 ~ 2025-08-31
INSERT INTO LEAVE (leave_id, leave_type, start_date, end_date, reason, status, user_id)
SELECT LEAVE_SEQ.NEXTVAL, 'LeaveOfAbsence', DATE '2025-08-20', DATE '2025-08-31', '개인 사정', 'PENDING', u.id
FROM USERS u WHERE u.email = 'user@aaa.com';

-- 연차 : admin@admin.com / 2025-09-02 ~ 2025-09-03
INSERT INTO LEAVE (leave_id, leave_type, start_date, end_date, reason, status, user_id)
SELECT LEAVE_SEQ.NEXTVAL, 'Annual', DATE '2025-09-02', DATE '2025-09-03', '가족 행사', 'APPROVED', u.id
FROM USERS u WHERE u.email = 'admin@admin.com';

-- 병가 : admin@admin.com / 2025-08-05 ~ 2025-08-06
INSERT INTO LEAVE (leave_id, leave_type, start_date, end_date, reason, status, user_id)
SELECT LEAVE_SEQ.NEXTVAL, 'Sick', DATE '2025-08-05', DATE '2025-08-06', '치과 치료', 'APPROVED', u.id
FROM USERS u WHERE u.email = 'admin@admin.com';