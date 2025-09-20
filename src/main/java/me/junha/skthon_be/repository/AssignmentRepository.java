package me.junha.skthon_be.repository;

import me.junha.skthon_be.entity.Assignment;
import me.junha.skthon_be.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByAdmin(Admin admin);  // 특정 관리자 작성 과제 조회
    List<Assignment> findByEndCheckFalse();     // 아직 종료되지 않은 과제 조회
    List<Assignment> findByStartAtBeforeAndEndAtAfter(LocalDateTime start, LocalDateTime end); // 특정 기간 내 과제
}
