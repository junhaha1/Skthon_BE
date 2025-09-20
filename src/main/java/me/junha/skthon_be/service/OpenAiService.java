package me.junha.skthon_be.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import me.junha.skthon_be.dto.ChatGPTRequest;
import me.junha.skthon_be.dto.ClientRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import reactor.util.retry.Retry;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OpenAiService {
    private final WebClient openAiWebClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${openai.model}")
    private String model;

    private final String apiUrl = "/chat/completions";

    /**
     * 사용자 요청을 받아 OpenAI 스트리밍 응답을 클라이언트로 전송합니다.
     */
    public void streamAnswerToClient(ClientRequest clientRequest, ResponseBodyEmitter emitter) {
        // 하드코딩된 프롬프트
        String systemPrompt = "당신은 유능한 어시스턴트입니다. 사용자의 질문에 친절하고 구체적으로 답하세요.";

        List<ChatGPTRequest.Message> messages = new ArrayList<>();
        messages.add(new ChatGPTRequest.Message("system", systemPrompt));

        if (clientRequest.getPreContent() != null) {
            messages.add(new ChatGPTRequest.Message("assistant", clientRequest.getPreContent()));
        }

        messages.add(new ChatGPTRequest.Message("user", clientRequest.getQuestion()));

        streamChat(messages, 1024, emitter); // maxTokens 값은 하드코딩 (예: 1024)
    }

    /**
     * OpenAI Chat API에 스트리밍 요청을 보내고 SSE로 클라이언트에 전달
     */
    private void streamChat(List<ChatGPTRequest.Message> messages,
                            int maxTokens,
                            ResponseBodyEmitter emitter) {

        ChatGPTRequest chatGPTRequest = new ChatGPTRequest(
                model,
                messages,
                maxTokens,
                0.7,   // temperature 하드코딩 (창의성 정도)
                true   // stream 옵션
        );

        openAiWebClient.post()
                .uri(apiUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .bodyValue(chatGPTRequest)
                .retrieve()
                .bodyToFlux(String.class)
                .retryWhen(
                        Retry.backoff(3, Duration.ofSeconds(5))
                                .filter(throwable -> {
                                    if (throwable instanceof WebClientResponseException) {
                                        int statusCode = ((WebClientResponseException) throwable).getRawStatusCode();
                                        return statusCode == 429 || statusCode >= 500;
                                    }
                                    return false;
                                })
                                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> retrySignal.failure())
                )
                .doOnNext(chunk -> {
                    try {
                        if (!"[DONE]".equals(chunk.trim())) {
                            emitter.send("data: " + chunk + "\n\n");

                            // 내부 로그 확인용
                            JsonNode jsonNode = objectMapper.readTree(chunk);
                            JsonNode delta = jsonNode.path("choices").get(0).path("delta");
                            JsonNode contentNode = delta.path("content");
                            if (!contentNode.isMissingNode() && !contentNode.isNull()) {
                                System.out.print(contentNode.asText());
                            }
                        }
                    } catch (IOException e) {
                        emitter.completeWithError(e);
                    }
                })
                .doOnError(emitter::completeWithError)
                .doOnComplete(() -> {
                    try {
                        emitter.send("data: [DONE]\n\n");
                    } catch (IOException e) {
                        emitter.completeWithError(e);
                    } finally {
                        emitter.complete();
                    }
                })
                .subscribe();
    }
}
