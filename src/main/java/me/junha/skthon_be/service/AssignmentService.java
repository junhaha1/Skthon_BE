package me.junha.skthon_be.service;
import lombok.RequiredArgsConstructor;
import me.junha.skthon_be.dto.assignment.AssignmentRequestDto;
import me.junha.skthon_be.dto.assignment.AssignmentResponseDto;
import me.junha.skthon_be.entity.Admin;
import me.junha.skthon_be.entity.Assignment;
import me.junha.skthon_be.repository.AdminRepository;
import me.junha.skthon_be.repository.AssignmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final AdminRepository adminRepository;

    /**
     * 과제 등록
     */
    @Transactional
    public AssignmentResponseDto registerAssignment(AssignmentRequestDto requestDto) {
        // 관리자 엔티티 조회
        Admin admin = adminRepository.findById(requestDto.getAdminId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 관리자 ID입니다."));

        // DTO → Entity 변환
        Assignment assignment = Assignment.builder()
                .admin(admin)
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .startAt(LocalDateTime.parse(requestDto.getStartAt()))
                .endAt(LocalDateTime.parse(requestDto.getEndAt()))
                .endCheck(requestDto.getEndCheck())
                .assignImage(requestDto.getAssignImage() != null
                        ? Base64.getDecoder().decode(requestDto.getAssignImage())
                        : null)
                .build();

        // 저장
        Assignment saved = assignmentRepository.save(assignment);

        // Entity → DTO 변환
        return toResponseDto(saved);
    }

    /**
     * 과제 전체 조회
     */
    @Transactional(readOnly = true)
    public List<AssignmentResponseDto> getAllAssignments() {
        return assignmentRepository.findAll().stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * 과제 단건 조회
     */
    @Transactional(readOnly = true)
    public AssignmentResponseDto getAssignmentById(Long id) {
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 과제 ID입니다."));
        return toResponseDto(assignment);
    }

    /**
     * 변환 로직 (Entity → DTO)
     */
    private AssignmentResponseDto toResponseDto(Assignment assignment) {
        return AssignmentResponseDto.builder()
                .id(assignment.getId())
                .title(assignment.getTitle())
                .content(assignment.getContent())
                .startAt(assignment.getStartAt().toString())
                .endAt(assignment.getEndAt().toString())
                .endCheck(assignment.getEndCheck())
                .assignImage(assignment.getAssignImage() != null
                        ? Base64.getEncoder().encodeToString(assignment.getAssignImage())
                        : null)
                .adminId(assignment.getAdmin().getId())
                .adminName(assignment.getAdmin().getName())
                .adminEmail(assignment.getAdmin().getEmail())
                .build();
    }


}
