package me.junha.skthon_be.service;

import lombok.RequiredArgsConstructor;
import me.junha.skthon_be.dto.company.CompanyRequestDto;
import me.junha.skthon_be.dto.company.CompanyResponseDto;
import me.junha.skthon_be.entity.Admin;
import me.junha.skthon_be.entity.Company;
import me.junha.skthon_be.entity.User;
import me.junha.skthon_be.repository.AdminRepository;
import me.junha.skthon_be.repository.CompanyRepository;
import me.junha.skthon_be.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CompanyRepository companyRepository;
    private final AdminRepository adminRepository;

    public User register(String email, String password, String name) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("이미 가입된 이메일입니다.");
        }
        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password)) // 비밀번호 해시
                .name(name)
                .build();
        return userRepository.save(user);
    }

    public User login(String email, String password) {
        return userRepository.findByEmail(email)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .orElseThrow(() -> new RuntimeException("이메일 또는 비밀번호가 올바르지 않습니다."));
    }

    //관리자 회원가입
    public Admin registerAdmin(String email, String password, String name, Long companyId) {
        // 이메일 중복 체크
        if (adminRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("이미 가입된 이메일입니다.");
        }

        // 회사 엔티티 조회
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회사 ID입니다."));

        // Admin 엔티티 생성
        Admin admin = Admin.builder()
                .email(email)
                .password(passwordEncoder.encode(password)) // 비밀번호 해시
                .name(name)
                .company(company) // FK 관계이므로 엔티티 주입
                .build();

        // 저장
        return adminRepository.save(admin);
    }

    //관리자 로그인
    public Admin loginAdmin(String email, String password) {
        return adminRepository.findByEmail(email)
                .filter(admin -> passwordEncoder.matches(password, admin.getPassword()))
                .orElseThrow(() -> new RuntimeException("이메일 또는 비밀번호가 올바르지 않습니다."));
    }


    //회사 등록하기
    public CompanyResponseDto registerCompany(CompanyRequestDto requestDto) {
        Company company = Company.builder()
                .category(requestDto.getCategory())
                .name(requestDto.getName())
                .logo(requestDto.getLogo() != null
                        ? Base64.getDecoder().decode(requestDto.getLogo())
                        : null)
                .build();

        // 저장
        Company saved = companyRepository.save(company);

        // Entity → DTO 변환
        return CompanyResponseDto.builder()
                .id(saved.getId())
                .category(saved.getCategory())
                .name(saved.getName())
                .logo(saved.getLogo() != null
                        ? Base64.getEncoder().encodeToString(saved.getLogo())
                        : null)
                .build();
    }



    public List<CompanyResponseDto> getAllCompanies() {
        List<Company> companies = companyRepository.findAll();

        return companies.stream()
                .map(company -> CompanyResponseDto.builder()
                        .id(company.getId())
                        .category(company.getCategory())
                        .name(company.getName())
                        .logo(company.getLogo() != null
                                ? Base64.getEncoder().encodeToString(company.getLogo())
                                : null)
                        .build())
                .collect(Collectors.toList());
    }


}
