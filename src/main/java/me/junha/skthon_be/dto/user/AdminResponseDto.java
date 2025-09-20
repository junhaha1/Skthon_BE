package me.junha.skthon_be.dto.user;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminResponseDto {
    private String email;
    private String name;
    private Long companyId;   // ID만 내려주거나
    private String companyName; // 회사 이름까지 내려주면 편리
}