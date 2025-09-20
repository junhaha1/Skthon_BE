package me.junha.skthon_be.service;

import lombok.RequiredArgsConstructor;
import me.junha.skthon_be.dto.proposal.ProposalRequestDto;
import me.junha.skthon_be.dto.proposal.ProposalResponseDto;
import me.junha.skthon_be.entity.Assignment;
import me.junha.skthon_be.entity.Proposal;
import me.junha.skthon_be.entity.User;
import me.junha.skthon_be.repository.AssignmentRepository;
import me.junha.skthon_be.repository.ProposalRepository;
import me.junha.skthon_be.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProposalService {

    private final ProposalRepository proposalRepository;
    private final UserRepository userRepository;
    private final AssignmentRepository assignmentRepository;

    /**
     * 제안 등록
     */
    @Transactional
    public ProposalResponseDto registerProposal(ProposalRequestDto requestDto) {
        // 사용자 조회
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자 ID입니다."));

        // 과제 조회 (nullable 허용)
        Assignment assignment = null;
        if (requestDto.getAssignId() != null) {
            assignment = assignmentRepository.findById(requestDto.getAssignId())
                    .orElseThrow(() -> new RuntimeException("존재하지 않는 과제 ID입니다."));
        }

        // DTO → Entity
        Proposal proposal = Proposal.builder()
                .user(user)
                .assignment(assignment)
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .updateAt(LocalDateTime.now()) // 기본값
                .selected(requestDto.getSelected())
                .build();

        // 저장
        Proposal saved = proposalRepository.save(proposal);

        // Entity → DTO
        return toResponseDto(saved);
    }

    /**
     * 전체 제안 조회
     */
    @Transactional(readOnly = true)
    public List<ProposalResponseDto> getAllProposals() {
        return proposalRepository.findAll().stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * 변환 메서드 (Entity → DTO)
     */
    private ProposalResponseDto toResponseDto(Proposal proposal) {
        return ProposalResponseDto.builder()
                .id(proposal.getId())
                .title(proposal.getTitle())
                .content(proposal.getContent())
                .updateAt(proposal.getUpdateAt().toString())
                .selected(proposal.getSelected())
                .userId(proposal.getUser().getId())
                .userName(proposal.getUser().getName())
                .userEmail(proposal.getUser().getEmail())
                .assignId(proposal.getAssignment() != null ? proposal.getAssignment().getId() : null)
                .assignTitle(proposal.getAssignment() != null ? proposal.getAssignment().getTitle() : null)
                .build();
    }
}
