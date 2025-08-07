-- 직급 (ranks)
INSERT INTO ranks (rankid, rankname) VALUES (1, '사원');
INSERT INTO ranks (rankid, rankname) VALUES (2, '주임');
INSERT INTO ranks (rankid, rankname) VALUES (3, '대리');
INSERT INTO ranks (rankid, rankname) VALUES (4, '과장');
INSERT INTO ranks (rankid, rankname) VALUES (5, '차장');
INSERT INTO ranks (rankid, rankname) VALUES (6, '부장');

-- 부서 (departments)
INSERT INTO departments (deptid, deptname) VALUES (1, '인사팀');
INSERT INTO departments (deptid, deptname) VALUES (2, '개발팀');
INSERT INTO departments (deptid, deptname) VALUES (3, '영업팀');
INSERT INTO departments (deptid, deptname) VALUES (4, '기획팀');
INSERT INTO departments (deptid, deptname) VALUES (5, '총무팀');

-- 근무유형 (worktypes)
INSERT INTO worktypes (worktypeid, worktypename) VALUES (1, '정규직');
INSERT INTO worktypes (worktypeid, worktypename) VALUES (2, '계약직');
INSERT INTO worktypes (worktypeid, worktypename) VALUES (3, '프리랜서');
INSERT INTO worktypes (worktypeid, worktypename) VALUES (4, '인턴');

--pw user123
INSERT INTO "USERS" (id, empnum, work_name, email, password, role, rankid, worktypeid, deptid, profileImageUrl, work_start_time, work_end_time)
VALUES (USER_SEQ.NEXTVAL,'a00000000' , '관리자', 'admin@admin.com', '$2a$10$uqEQ/3hmmkdOJgrfRQz8seCCzPpWUwJijt2nD78/tcv5VTTqnif4m', 'ADMIN', 6, 1, 5, 'https://img.daisyui.com/images/profile/demo/yellingcat@192.webp', '09:00', '18:00');
INSERT INTO "USERS" (id, empnum, work_name, email, password, role, rankid, worktypeid, deptid, profileImageUrl, work_start_time, work_end_time)
VALUES (USER_SEQ.NEXTVAL,'a123456789' , '테스터', 'user@aaa.com', '$2a$10$i4RvLbeZk/bxEFA8NXk9YunSIwJi5mVwhsHQ.qtT7gakz.Xvmj5/W', 'USER', 1, 1, 1, 'https://img.daisyui.com/images/profile/demo/yellingcat@192.webp', '09:00', '18:00');
