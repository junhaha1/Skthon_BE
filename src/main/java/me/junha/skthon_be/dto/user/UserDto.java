package me.junha.skthon_be.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserDto {
    private String email;
    private String password;
    private String name;
}
