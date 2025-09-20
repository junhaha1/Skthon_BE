package me.junha.skthon_be.dto.user;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDto {
    private String email;
    private String name;
    private String password;   // 실제 엔티티에 없다면 DTO에서만 사용 가능
    private Long companyId;    // 어떤 회사 소속인지
}

