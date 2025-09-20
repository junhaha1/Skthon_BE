package me.junha.skthon_be.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import me.junha.skthon_be.dto.openai.ChatGPTRequest;
import me.junha.skthon_be.dto.openai.ClientRequest;
import me.junha.skthon_be.dto.openai.SummaryRequest;
import me.junha.skthon_be.entity.Assignment;
import me.junha.skthon_be.repository.AssignmentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
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
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OpenAiService {
    private final WebClient openAiWebClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final AssignmentRepository assignmentRepository;
    @Value("${openai.model}")
    private String model;

    private final String apiUrl = "/chat/completions";

    /**
     * 사용자 요청을 받아 OpenAI 스트리밍 응답을 클라이언트로 전송합니다.
     */
    public void streamAnswerToClient(ClientRequest clientRequest, ResponseBodyEmitter emitter) {
        // 하드코딩된 프롬프트
        String systemPrompt = "당신은 유능한 어시스턴트입니다. 사용자의 질문에 친절하고 구체적으로 답하세요." + clientRequest.getAssignmentContent();
        String advice = "해당 공모전 내용에 대해서만 답변해줘. 다른 질문을 하면 공모전 내용에 대한 질문만 하도록 해줘.";

        List<ChatGPTRequest.Message> messages = new ArrayList<>();
        messages.add(new ChatGPTRequest.Message("system", systemPrompt + advice));

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
    public String summaryChatGPT(SummaryRequest clientSummaryRequest) {
        // 1. 하드코딩된 프롬프트 작성
        String systemPrompt = """
    당신은 공모전 기획 보조 AI입니다.
    아래 대화 내용을 읽고, 공모전 주제에 맞게 정리해 주세요.
    
    ⚠️ 출력은 반드시 아래 양식에 맞춰주세요. 번호 대신 섹션 제목을 그대로 써야 합니다.
    
    [출력 형식]
    === 핵심 아이디어 ===
    - (핵심 아이디어를 한두 문장으로)
    - (필요하다면 추가 아이디어를 bullet로 나열)
    
    === 문제의식 및 배경 ===
    - (해당 문제를 인식하게 된 구체적 배경)
    - (데이터, 사회적 현상 등 근거를 bullet로)
    
    === 기대 효과 ===
    - (기대되는 긍정적 효과를 bullet로 나열)
    - (정책, 캠페인, 사회적 파급 효과 등)
    """;

        // 2. 과제 내용 조회
        Assignment assignment = assignmentRepository.findById(clientSummaryRequest.getAssignmentId())
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 과제를 찾을 수 없습니다."));

        // 3. 메시지 초기화
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", systemPrompt + assignment.getContent()));
        // 사용자가 입력한 전체 대화 내용도 함께 추가
        messages.add(Map.of("role", "user", "content", clientSummaryRequest.getTotalContent()));

        // 4. ChatGPT 호출 및 응답 처리
        StringBuilder fullAnswer = new StringBuilder();
        boolean isFinished = false;

        while (!isFinished) {
            Map<String, Object> chatGPTRequest = Map.of(
                    "model", model,
                    "messages", messages,
                    "max_tokens", 1000,
                    "temperature", 0.7,
                    "stream", false
            );

            Map<String, Object> response = openAiWebClient.post()
                    .uri(apiUrl)
                    .bodyValue(chatGPTRequest)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

            // 응답 파싱
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            Map<String, Object> firstChoice = choices.get(0);
            Map<String, String> message = (Map<String, String>) firstChoice.get("message");

            String answer = message.get("content");
            String finishReason = (String) firstChoice.get("finish_reason");

            fullAnswer.append(answer);

            if ("length".equals(finishReason)) {
                messages.add(Map.of("role", "assistant", "content", answer));
                messages.add(Map.of("role", "system", "content", "이어서 계속 요약"));
            } else {
                isFinished = true;
            }
        }

        return fullAnswer.toString();
    }


    public String titleChatGPT(String summary) {
        // 1. 하드코딩된 프롬프트 작성
        String systemPrompt = """
        당신은 공모전 주제 요약을 바탕으로 매력적이고 간결한 제목을 만드는 보조 AI입니다.
        아래 요약을 읽고, 적합한 제목을 1줄로만 생성하세요.
        """;

        // 2. 요청 본문 구성 (Map 활용)
        Map<String, Object> chatGPTRequest = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", summary)
                ),
                "max_tokens", 100,
                "temperature", 0.4,
                "stream", false
        );

        // 3. API 호출
        Map<String, Object> response = openAiWebClient.post()
                .uri(apiUrl)
                .bodyValue(chatGPTRequest)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();

        // 4. 응답 파싱
        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
        Map<String, Object> firstChoice = choices.get(0);
        Map<String, String> message = (Map<String, String>) firstChoice.get("message");

        String title = message.get("content");

        return title;
    }


}
