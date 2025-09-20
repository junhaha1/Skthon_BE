package me.junha.skthon_be.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.junha.skthon_be.dto.ApiResponse;
import me.junha.skthon_be.dto.proposal.ProposalRequestDto;
import me.junha.skthon_be.dto.proposal.ProposalResponseDto;
import me.junha.skthon_be.service.ProposalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Proposal", description = "공모전 제안 등록 및 조회 API")
public class ProposalController {

    private final ProposalService proposalService;

    @PostMapping("/proposals")
    @Operation(summary = "제안 등록", description = "사용자가 공모전 과제에 대한 제안을 등록합니다.")
    public ResponseEntity<ApiResponse<ProposalResponseDto>> registerProposal(
            @RequestBody ProposalRequestDto requestDto) {
        ProposalResponseDto proposal = proposalService.registerProposal(requestDto);
        return ResponseEntity.ok(new ApiResponse<>("제안 등록 성공", proposal));
    }

    @GetMapping("/proposals")
    @Operation(summary = "제안 전체 조회", description = "등록된 모든 제안을 조회합니다.")
    public ResponseEntity<ApiResponse<List<ProposalResponseDto>>> getAllProposals() {
        List<ProposalResponseDto> proposals = proposalService.getAllProposals();
        return ResponseEntity.ok(new ApiResponse<>("조회 성공", proposals));
    }

    @GetMapping("/proposals/check")
    @Operation(summary = "제안 제출 여부 확인", description = "사용자가 특정 과제에 이미 제안을 제출했는지 여부를 확인합니다.")
    public ResponseEntity<ApiResponse<Boolean>> checkProposal(
            @RequestParam Long userId,
            @RequestParam Long assignId) {
        boolean submitted = proposalService.hasUserSubmittedProposal(userId, assignId);
        return ResponseEntity.ok(new ApiResponse<>("확인 성공", submitted));
    }

    @GetMapping("/proposals/assignment/{assignId}")
    @Operation(summary = "과제별 제안 조회", description = "특정 과제 ID에 대한 모든 제안을 조회합니다.")
    public ResponseEntity<ApiResponse<List<ProposalResponseDto>>> getProposalsByAssignId(
            @PathVariable Long assignId) {
        List<ProposalResponseDto> proposals = proposalService.getProposalsByAssignId(assignId);
        return ResponseEntity.ok(new ApiResponse<>("조회 성공", proposals));
    }

    @PatchMapping("/proposals/{id}/selected")
    @Operation(summary = "제안서 채택 여부 수정", description = "제안서의 selected 값을 true/false로 수정합니다.")
    public ResponseEntity<ApiResponse<ProposalResponseDto>> updateSelected(
            @PathVariable Long id,
            @RequestParam boolean selected) {

        ProposalResponseDto updatedProposal = proposalService.updateProposalSelected(id, selected);
        return ResponseEntity.ok(new ApiResponse<>("제안서 selected 수정 성공", updatedProposal));
    }
}
