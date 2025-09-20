package me.junha.skthon_be.controller;

import lombok.RequiredArgsConstructor;
import me.junha.skthon_be.dto.openai.ClientRequest;
import me.junha.skthon_be.dto.openai.SummaryRequest;
import me.junha.skthon_be.service.OpenAiService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

@RestController
@RequiredArgsConstructor
public class AiController {

    private final OpenAiService openAiService;

    @PostMapping(value = "/answer/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseBodyEmitter streamAnswer(@RequestHeader("Authorization") String authHeader, @RequestBody ClientRequest clientRequest) {

        ResponseBodyEmitter emitter = new ResponseBodyEmitter();
        openAiService.streamAnswerToClient(clientRequest, emitter);
        return emitter;
    }

    @PostMapping(value = "/answer/summaryChat", produces = MediaType.TEXT_PLAIN_VALUE)
    public String summaryChat(@RequestHeader("Authorization") String authHeader, @RequestBody SummaryRequest summaryRequest) {
        return openAiService.summaryChatGPT(summaryRequest);
    }

}
