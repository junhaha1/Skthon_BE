package me.junha.skthon_be.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.junha.skthon_be.dto.ApiResponse;
import me.junha.skthon_be.dto.assignment.AssignmentRequestDto;
import me.junha.skthon_be.dto.assignment.AssignmentResponseDto;
import me.junha.skthon_be.service.AssignmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Assignment", description = "공모전 게시글 등록, 조회, 수정, 삭제")
public class AssignmentController {

    private final AssignmentService assignmentService;

    @PostMapping("/register/assignments")
    @Operation(summary = "과제 등록", description = "관리자가 공모전 과제를 등록합니다.")
    public ResponseEntity<ApiResponse<AssignmentResponseDto>> registerAssignment(
            @RequestBody AssignmentRequestDto requestDto) {
        AssignmentResponseDto assignment = assignmentService.registerAssignment(requestDto);
        return ResponseEntity.ok(new ApiResponse<>("과제 등록 성공", assignment));
    }

    @GetMapping("/assignments")
    @Operation(summary = "과제 전체 조회", description = "등록된 모든 과제를 조회합니다.")
    public ResponseEntity<ApiResponse<List<AssignmentResponseDto>>> getAllAssignments() {
        List<AssignmentResponseDto> assignments = assignmentService.getAllAssignments();
        return ResponseEntity.ok(new ApiResponse<>("조회 성공", assignments));
    }

    @GetMapping("/assignments/{id}")
    @Operation(summary = "과제 단건 조회", description = "선택한 ID에 해당하는 과제를 조회합니다.")
    public ResponseEntity<ApiResponse<AssignmentResponseDto>> getAssignmentById(@PathVariable Long id) {
        AssignmentResponseDto assignment = assignmentService.getAssignmentById(id);
        return ResponseEntity.ok(new ApiResponse<>("조회 성공", assignment));
    }

    @GetMapping("/assignments/admin/{adminId}")
    @Operation(summary = "관리자 공고 조회", description = "해당 관리자가 올린 모든 과제를 조회합니다.")
    public ResponseEntity<ApiResponse<List<AssignmentResponseDto>>> getAssignmentsByAdminId(
            @PathVariable Long adminId) {
        List<AssignmentResponseDto> assignments = assignmentService.getAssignmentsByAdminId(adminId);
        return ResponseEntity.ok(new ApiResponse<>("조회 성공", assignments));
    }
}
