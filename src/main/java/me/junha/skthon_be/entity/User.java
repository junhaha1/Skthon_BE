package me.junha.skthon_be.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Lob
    @Column(name = "file_data")   // DB 스키마에 맞춤
    private byte[] fileData;

    @Column(name = "create_at", insertable = false, updatable = false) // DB 기본값 사용
    private LocalDateTime createAt;
}
