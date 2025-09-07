package com.example.Attendance.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.Attendance.dto.WorkingRowDTO;
import com.example.Attendance.entity.User;
import com.example.Attendance.entity.User.Role;

public interface UserRepository extends JpaRepository<User, Long>{
	Optional<User> findByEmail(String email);
	Optional<User>	findByEmpnum(String empnum);
	boolean existsByEmail(String email);
	boolean existsByEmpnum(String empnum);
	//int deleteByEmpnum(String empnum);
	long countByRoleAndIsDeleted(User.Role role, User.Delete isDeleted);

	
	// Soft Delete
	@Query("SELECT u FROM User u WHERE u.isDeleted = 'NOTDELETE'")
    List<User> findAllByIsDeleted(User.Delete isDeleted);
    Page<User> findAllByIsDeleted(User.Delete isDeleted, Pageable pageable);

	Optional<User> findByEmailAndIsDeleted(String email, User.Delete isDeleted);
	Optional<User> findByEmpnumAndIsDeleted(String empnum, User.Delete isDeleted);

	
	// 오늘 상태 계산: Attendance / Leave 를 LEFT JOIN
	@Query(
		    "select new com.example.Attendance.dto.WorkingRowDTO(" +
		    "  u.id, " +
		    "  u.empnum, " +
		    "  u.name, " +
		    "  u.dept.deptname, " +
		    "  a.clockIn, " +
		    "  a.clockOut, " +
		    "  case " +
		    "    when l.id is not null then 'LEAVE' " +
		    "    when a.id is null then 'ABSENT' " +
		    "    when a.clockOut is not null then 'LEFT' " +
		    "    when a.clockIn is not null and a.clockOut is null then 'PRESENT' " +
		    "    else 'ABSENT' " +
		    "  end" +
		    ") " +
		    "from User u " +
		    "left join Attendance a on a.user = u and a.date = :date " +
		    "left join Leave l on l.user = u and :date between l.startDate and l.endDate " +
		    "where u.role = 'USER' and u.isDeleted = 'NOTDELETE'"
		)
	Page<WorkingRowDTO> findWorkingList(@Param("date") LocalDate date, Pageable pageable);
	
	
	@Query(
		    "select new com.example.Attendance.dto.WorkingRowDTO(" +
		    "  u.id, " +
		    "  u.empnum, " +
		    "  u.name, " +
		    "  u.dept.deptname, " +
		    "  a.clockIn, " +
		    "  a.clockOut, " +
		    "  case " +
		    "    when l.id is not null then 'LEAVE' " +
		    "    when a.id is null then 'ABSENT' " +
		    "    when a.clockOut is not null then 'LEFT' " +
		    "    when a.clockIn is not null and a.clockOut is null then 'PRESENT' " +
		    "    else 'ABSENT' " +
		    "  end" +
		    ") " +
		    "from User u " +
		    "left join Attendance a on a.user = u and a.date = :date " +
		    "left join Leave l on l.user = u and :date between l.startDate and l.endDate " +
		    "where u.role = 'USER' " +
		    "and u.isDeleted = 'NOTDELETE'"+
		    "and ( " +
		    "       :status is null " +
		    "    or (:status = 'LEAVE'   and l.id is not null) " +
		    "    or (:status = 'ABSENT'  and l.id is null and a.id is null) " +
		    "    or (:status = 'LEFT'    and l.id is null and a.clockOut is not null) " +
		    "    or (:status = 'PRESENT' and l.id is null and a.clockIn is not null and a.clockOut is null) " +
		    "  )"
		)
		Page<WorkingRowDTO> findWorkingListByStatus(
		    @Param("date") LocalDate date,
		    @Param("status") String status, // PRESENT | LEFT | ABSENT | LEAVE (대문자 권장)
		    Pageable pageable
		);
	

}
