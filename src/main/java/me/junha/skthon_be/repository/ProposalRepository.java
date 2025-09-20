package me.junha.skthon_be.repository;

import me.junha.skthon_be.entity.Proposal;
import me.junha.skthon_be.entity.User;
import me.junha.skthon_be.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProposalRepository extends JpaRepository<Proposal, Long> {
    List<Proposal> findByUser(User user);                      // 특정 유저의 제안 조회
    List<Proposal> findByAssignment(Assignment assignment);    // 특정 과제에 대한 제안 조회
    List<Proposal> findByAssignment_Id(Long assignId);         // 특정 과제 ID로 제안 조회
    List<Proposal> findBySelected(String selected);            // 채택 상태별 조회
    Optional<Proposal> findByUserIdAndAssignment_Id(Long userId, Long assignmentId);
}
