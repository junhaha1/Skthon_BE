package me.junha.skthon_be.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {
    private String message;
    private T data;   // 성공 시 필요한 데이터 (없으면 null)
}
