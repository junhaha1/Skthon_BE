package me.junha.skthon_be.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "company")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT 매핑
    private Long id;

    @Column(nullable = false, length = 100)
    private String category;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Lob
    private byte[] logo; // LONGBLOB → byte[] 로 매핑
}
