package me.junha.skthon_be.dto.assignment;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentRequestDto {
    private Long adminId;       // 어떤 관리자가 작성했는지
    private String title;       // 제목
    private String content;     // 내용
    private String startAt;     // 시작일 (ISO 8601 문자열: "2025-09-21T10:00:00")
    private String endAt;       // 종료일
    private Boolean endCheck;   // 종료 여부
    private String assignImage; // Base64 문자열 (파일 업로드 고려 시 변경 가능)
}
