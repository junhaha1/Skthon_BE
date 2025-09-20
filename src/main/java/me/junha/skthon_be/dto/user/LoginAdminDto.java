package me.junha.skthon_be.dto.user;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginAdminDto {
    private String email;
    private String password;
}
