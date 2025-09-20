package me.junha.skthon_be.dto.proposal;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProposalResponseDto {
    private Long id;
    private String title;
    private String content;
    private String updateAt;     // LocalDateTime → String 변환해서 내려줌
    private String selected;

    // 작성자(User) 정보
    private Long userId;
    private String userName;
    private String userEmail;

    // 연결된 과제(Assignment) 정보
    private Long assignId;
    private String assignTitle;
}
