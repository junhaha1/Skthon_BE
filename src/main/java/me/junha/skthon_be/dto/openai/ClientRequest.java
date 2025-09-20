package me.junha.skthon_be.dto.openai;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
//클라이언트에서 보낸 요청 질문
public class ClientRequest {
    private int promptLevel;
    private String preContent;
    private String question;
}
