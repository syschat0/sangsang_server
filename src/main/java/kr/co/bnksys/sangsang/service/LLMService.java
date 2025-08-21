package kr.co.bnksys.sangsang.service;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import kr.co.bnksys.sangsang.config.LLMProperties;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Type;
import java.util.*;

@Service
public class LLMService {

    //private static final String OLLAMA_URL  = "http://localhost:11434/api/chat";
    //private static final String MODEL_NAME  = "gemma3:27b";
    private static final int    TOTAL_QUESTIONS = 20;

    private final LLMProperties props;

    public LLMService(LLMProperties props) {
        this.props = props;
    }

    private final RestTemplate rest = new RestTemplate();
    private final Gson gson = new GsonBuilder().serializeNulls().create();

    // === 질문 생성 ===
    public String generateNextQuestion(int index, String lastAnswer) {
        String system = "당신은 면접 질문 생성기입니다. 사용자 답변을 바탕으로 " +
                "지원자의 성향/행동/가치관을 파악할 수 있는 한국어 '질문 한 문장'만 출력하세요. " +
                "불필요한 설명 없이 질문만 출력합니다.";

        String user = String.format(
                "지금은 %d번째 질문입니다(총 %d문항). 직전 답변: \"%s\".\n" +
                        "지원자의 성향/행동/가치관을 더 깊게 파악할 수 있는 질문 한 문장을 한국어로 간결하게 만들어 주세요. " +
                        "질문 외의 텍스트는 출력하지 마세요.",
                index, TOTAL_QUESTIONS, lastAnswer == null ? "" : lastAnswer.trim()
        );

        String content = callOllama(system, user, false);
        if (content == null || content.isBlank()) {
            throw new IllegalStateException("Ollama returned empty question");
        }
        return content.trim();
    }

    // === 최종 평가(JSON만 강제) → Map<String, Double>로 반환 ===
    public Map<String, Double> evaluate(List<String> answers) {
        String joined = String.join("\n", answers);

        String system = "당신은 심리/역량 평가 분석가입니다. 사용자의 답변을 평가하여 " +
                "지정된 15개 항목을 0~5의 값으로 점수화합니다. 오직 JSON 객체만 출력합니다. " +
                "코드펜스나 다른 텍스트를 포함하지 마세요.";

        String user = "다음은 한 지원자의 스무고개 형식 Q/A 답변 모음입니다. 이를 바탕으로 15개 항목을 0~5점으로 평가해 JSON만 출력하세요.\n" +
                "항목: [공감사회기술, 성실성, 개방성, 외향성, 우호성, 정서안정성, 기술전문성, 인지문제해결, 대인영향력, 자기관리, 적응력, 학습속도, 대인민첩성, 성과민첩성, 자기인식, 자기조절]\n" +
                "예시: {\"공감사회기술\":5, \"성실성\":3, ...}\n" +
                "설명 없이 JSON만:\n\n=== 답변 시작 ===\n" + joined + "\n=== 답변 끝 ===";

        String content = callOllama(system, user, true); // format=json

        return parseScores(content);
    }

    // === Ollama Chat API 호출 ===
    private String callOllama(String system, String user, boolean forceJson) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", props.getModelName());
        body.put("messages", List.of(
                Map.of("role", "system", "content", system),
                Map.of("role", "user", "content", user)
        ));
        body.put("stream", false);
        if (forceJson) {
            body.put("format", "json"); // JSON 강제
        }

        ResponseEntity<Map> res = rest.postForEntity(props.getOllamaUrl(), new HttpEntity<>(body, headers), Map.class);
        if (!res.getStatusCode().is2xxSuccessful() || res.getBody() == null) return null;

        Object msg = res.getBody().get("message");
        if (!(msg instanceof Map)) return null;
        Object content = ((Map<?, ?>) msg).get("content");
        return (content instanceof String) ? (String) content : null;
    }

    // === JSON 안전 파싱 ===
    private Map<String, Double> parseScores(String raw) {
        Map<String, Double> out = new LinkedHashMap<>();
        if (raw == null) return out;

        String t = cleanupToJson(raw);

        // 1) 바로 Map<String, Double>
        try {
            Type t1 = new TypeToken<Map<String, Double>>(){}.getType();
            Map<String, Double> m = gson.fromJson(t, t1);
            if (m != null) return m;
        } catch (Exception ignore) {}

        // 2) Map<String, Object> → Double 강제
        try {
            Type t2 = new TypeToken<Map<String, Object>>(){}.getType();
            Map<String, Object> any = gson.fromJson(t, t2);
            if (any != null) {
                for (var e : any.entrySet()) {
                    Double v = coerceDouble(e.getValue());
                    if (v != null) out.put(e.getKey(), v);
                }
            }
        } catch (Exception ignore) {}
        return out;
    }

    private Double coerceDouble(Object v) {
        if (v == null) return null;
        if (v instanceof Number) return ((Number) v).doubleValue();
        if (v instanceof String s) {
            try { return Double.parseDouble(s.trim()); } catch (Exception ignore) {}
        }
        return null;
    }

    private String cleanupToJson(String s) {
        String t = s.trim();
        // ```json ... ``` 제거
        t = t.replaceAll("(?s)```json\\s*(.*?)\\s*```", "$1");
        t = t.replaceAll("(?s)```\\s*(.*?)\\s*```", "$1").trim();

        // 이중 인코딩 JSON: "{"a":1}" → {"a":1}
        if (t.startsWith("\"") && t.endsWith("\"")) {
            try {
                String unq = gson.fromJson(t, String.class);
                if (unq != null) t = unq.trim();
            } catch (Exception ignore) {}
        }

        // 앞에 설명이 붙은 경우 첫 { ... }만 추출
        if (!t.startsWith("{")) {
            int start = t.indexOf('{');
            if (start >= 0) {
                int depth = 0;
                for (int i = start; i < t.length(); i++) {
                    char c = t.charAt(i);
                    if (c == '{') depth++;
                    else if (c == '}') {
                        depth--;
                        if (depth == 0) {
                            t = t.substring(start, i + 1);
                            break;
                        }
                    }
                }
            }
        }
        return t;
    }
}
