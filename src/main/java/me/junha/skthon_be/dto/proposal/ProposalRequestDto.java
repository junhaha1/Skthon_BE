package me.junha.skthon_be.dto.proposal;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProposalRequestDto {
    private Long userId;      // 제안 작성자
    private Long assignId;    // 어떤 과제에 대한 제안인지 (nullable 가능)
    private String title;     // 제안 제목
    private String content;   // 제안 내용
    private String selected;  // 채택 상태 (예: "PENDING", "SELECTED", "REJECTED")
}