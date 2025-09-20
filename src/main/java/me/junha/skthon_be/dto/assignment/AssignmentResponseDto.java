package me.junha.skthon_be.dto.assignment;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentResponseDto {
    private Long id;            // 과제 ID
    private String title;
    private String content;
    private String startAt;
    private String endAt;
    private Boolean endCheck;
    private String assignImage; // Base64 문자열
    private Long adminId;       // 작성 관리자 ID
    private String adminName;   // 작성 관리자 이름
    private String adminEmail;  // 관리자 이메일
}
