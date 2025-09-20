package me.junha.skthon_be.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.junha.skthon_be.dto.ApiResponse;
import me.junha.skthon_be.dto.user.LoginUserDto;
import me.junha.skthon_be.dto.user.UserDto;
import me.junha.skthon_be.dto.user.UserResponseDto;
import me.junha.skthon_be.entity.User;
import me.junha.skthon_be.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "회원가입 및 로그인 API")
public class AuthController {

    private final UserService userService;

    // 회원가입
    @Operation(summary = "회원가입", description = "이메일, 비밀번호, 이름으로 회원가입을 진행합니다.")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@RequestBody UserDto userDto) {
        userService.register(userDto.getEmail(), userDto.getPassword(), userDto.getName());
        return ResponseEntity.ok(new ApiResponse<>("회원가입이 완료되었습니다.", null));
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "이메일과 비밀번호를 입력하면 회원 정보를 반환합니다.")
    public ResponseEntity<ApiResponse<UserResponseDto>> login(@RequestBody LoginUserDto loginDto) {
        User user = userService.login(loginDto.getEmail(), loginDto.getPassword());

        // 필요한 정보만 담아서 응답
        UserResponseDto responseDto = new UserResponseDto(
                user.getId(),
                user.getEmail(),
                user.getName()
        );

        return ResponseEntity.ok(new ApiResponse<>("로그인 성공", responseDto));
    }

    @PostMapping("/register/admin")
    public ResponseEntity<ApiResponse<Void>> registerAdmin(@RequestBody UserDto userDto) {
        userService.register(userDto.getEmail(), userDto.getPassword(), userDto.getName());
        return ResponseEntity.ok(new ApiResponse<>("회원가입이 완료되었습니다.", null));
    }

    @PostMapping("/login/admin")
    public ResponseEntity<ApiResponse<UserResponseDto>> loginAdmin(@RequestBody LoginUserDto loginDto) {
        User user = userService.login(loginDto.getEmail(), loginDto.getPassword());

        // 필요한 정보만 담아서 응답
        UserResponseDto responseDto = new UserResponseDto(
                user.getId(),
                user.getEmail(),
                user.getName()
        );

        return ResponseEntity.ok(new ApiResponse<>("로그인 성공", responseDto));
    }

    @PostMapping("/register/company")
    public ResponseEntity<ApiResponse<UserResponseDto>> registerCompany(@RequestBody UserDto userDto) {
        userService.register(userDto.getEmail(), userDto.getPassword(), userDto.getName());
        return null;
    }
}
