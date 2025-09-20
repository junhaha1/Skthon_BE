package me.junha.skthon_be.dto.openai;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SummaryRequest {
    private Long assignmentId;
    private String totalContent;
}
