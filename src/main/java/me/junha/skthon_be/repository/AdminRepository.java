package me.junha.skthon_be.repository;

import me.junha.skthon_be.entity.Admin;
import me.junha.skthon_be.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByEmail(String email);   // 이메일로 검색
    List<Admin> findByCompany(Company company);  // 특정 회사 소속 관리자 조회
}
