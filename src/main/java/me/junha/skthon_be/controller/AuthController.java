package me.junha.skthon_be.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.junha.skthon_be.dto.ApiResponse;
import me.junha.skthon_be.dto.company.CompanyRequestDto;
import me.junha.skthon_be.dto.company.CompanyResponseDto;
import me.junha.skthon_be.dto.user.*;
import me.junha.skthon_be.entity.Admin;
import me.junha.skthon_be.entity.User;
import me.junha.skthon_be.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "회원가입 및 로그인 API")
public class AuthController {

    private final AuthService authService;

    // 회원가입
    @Operation(summary = "회원가입", description = "이메일, 비밀번호, 이름으로 회원가입을 진행합니다.")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@RequestBody UserDto userDto) {
        authService.register(userDto.getEmail(), userDto.getPassword(), userDto.getName());
        return ResponseEntity.ok(new ApiResponse<>("회원가입이 완료되었습니다.", null));
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "이메일과 비밀번호를 입력하면 회원 정보를 반환합니다.")
    public ResponseEntity<ApiResponse<UserResponseDto>> login(@RequestBody LoginUserDto loginDto) {
        User user = authService.login(loginDto.getEmail(), loginDto.getPassword());

        // 필요한 정보만 담아서 응답
        UserResponseDto responseDto = new UserResponseDto(
                user.getId(),
                user.getEmail(),
                user.getName()
        );

        return ResponseEntity.ok(new ApiResponse<>("로그인 성공", responseDto));
    }

    @PostMapping("/register/admin")
    @Operation(summary = "관리자 등록", description = "이메일, 비밀번호, 이름으로 회원가입을 진행합니다.")
    public ResponseEntity<ApiResponse<Void>> registerAdmin(@RequestBody AdminDto adminDto) {
        authService.registerAdmin(adminDto.getEmail(), adminDto.getPassword(), adminDto.getName(), adminDto.getCompanyId());
        return ResponseEntity.ok(new ApiResponse<>("회원가입이 완료되었습니다.", null));
    }

    @PostMapping("/login/admin")
    @Operation(summary = "관리자 로그인", description = "이메일과 비밀번호를 입력하면 관리자 정보를 반환합니다.")
    public ResponseEntity<ApiResponse<AdminResponseDto>> loginAdmin(@RequestBody LoginAdminDto loginDto) {
        Admin admin = authService.loginAdmin(loginDto.getEmail(), loginDto.getPassword());

        // 필요한 정보만 담아서 응답
        AdminResponseDto responseDto = AdminResponseDto.builder()
                .email(admin.getEmail())
                .name(admin.getName())
                .companyId(admin.getCompany().getId())
                .companyName(admin.getCompany().getName()) // 회사명 추가
                .build();

        return ResponseEntity.ok(new ApiResponse<>("로그인 성공", responseDto));
    }

    @PostMapping("/register/company")
    @Operation(summary = "회사 등록", description = "회사정보를 등록")
    public ResponseEntity<ApiResponse<CompanyResponseDto>> registerCompany(@RequestBody CompanyRequestDto companyDto) {
        authService.registerCompany(companyDto);
        return ResponseEntity.ok(new ApiResponse<>("회사가 등록되었습니다.", null));
    }

    @GetMapping("/companies")
    @Operation(summary = "회사 전체 조회", description = "등록된 모든 회사 정보를 조회")
    public ResponseEntity<ApiResponse<List<CompanyResponseDto>>> getAllCompanies() {
        List<CompanyResponseDto> companies = authService.getAllCompanies();
        return ResponseEntity.ok(new ApiResponse<>("조회 성공", companies));
    }
}
