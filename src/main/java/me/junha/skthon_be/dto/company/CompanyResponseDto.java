package me.junha.skthon_be.dto.company;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyResponseDto {
    private Long id;
    private String category;
    private String name;
    private String logo; // base64 문자열 (or null)
}
