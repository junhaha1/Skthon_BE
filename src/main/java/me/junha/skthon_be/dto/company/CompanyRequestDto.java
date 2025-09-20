package me.junha.skthon_be.dto.company;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyRequestDto {
    private String category;
    private String name;
    private String logo; // base64 인코딩된 문자열 (파일 업로드 고려 시 변경 가능)
}
