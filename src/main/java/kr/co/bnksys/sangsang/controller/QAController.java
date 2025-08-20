package kr.co.bnksys.sangsang.controller;

import kr.co.bnksys.sangsang.mapper.QaMapper;
import kr.co.bnksys.sangsang.model.QuestionAnswer;
import kr.co.bnksys.sangsang.model.UserSession;
import kr.co.bnksys.sangsang.repository.QARepository;
import kr.co.bnksys.sangsang.repository.UserSessionRepository;
import kr.co.bnksys.sangsang.service.LLMService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin("/*")
public class QAController {

    private static final Logger log = LoggerFactory.getLogger(QAController.class);
    private static final int TOTAL = 20;

    private final QARepository qaRepository;
    private final UserSessionRepository userSessionRepository;
    private final LLMService llmService;


    @Autowired
    QaMapper qaMapper;

    public QAController(QARepository qaRepository,
                        UserSessionRepository userSessionRepository,
                        LLMService llmService) {
        this.qaRepository = qaRepository;
        this.userSessionRepository = userSessionRepository;
        this.llmService = llmService;
    }

    // 세션 발급
    @PostMapping("/session")
    public Map<String, Object> createSession() {
        UserSession s = userSessionRepository.save(new UserSession());
        return Map.of("sessionId", s.getSessionId());
    }

    @PostMapping("/answer")
    public Map<String, Object> submitAnswer(@RequestParam String sessionId,
                                            @RequestParam(required = false) String answer) {

        // 지금까지 저장된 레코드 수
        long count = qaRepository.countBySessionId(sessionId);

        // 1) 첫 호출: 첫 질문 생성
        if (count == 0) {
            String q = safeGenerate(1, "");
            saveQuestion(sessionId, q);
            return Map.of("done", false, "question", q, "index", 1, "total", TOTAL);
        }

        // 2) 마지막 질문 row 가져오기
        QuestionAnswer lastQA = qaRepository.findTopBySessionIdOrderByIdDesc(sessionId)
                .orElseThrow(() -> new RuntimeException("질문을 찾을 수 없음"));

        // 3) 답변 저장 (UPDATE)
        if (answer != null && !answer.isBlank() && lastQA.getAnswerText() == null) {
            lastQA.setAnswerText(answer);
            qaRepository.save(lastQA);
        }

        // 4) 완료 여부 확인
        long answeredCount = qaRepository.countBySessionIdAndAnswerTextIsNotNull(sessionId);
        if (answeredCount >= TOTAL) {
            List<String> answers = qaRepository.findBySessionIdOrderByIdAsc(sessionId).stream()
                    .map(QuestionAnswer::getAnswerText)
                    .filter(v -> v != null && !v.isBlank())
                    .toList();

            Map<String,Double> scores = llmService.evaluate(answers);

            System.out.println("완료!!! : " + scores.toString());
            HashMap paramMap = new HashMap();
            paramMap.putAll(scores);
            paramMap.put("email",sessionId);

            if ("Y".equals(qaMapper.selectDataDupYn(paramMap))){
                qaMapper.deleteResultData(paramMap);
            }
            qaMapper.insertResultData(paramMap);

            return Map.of("done", true, "scores", scores, "index", TOTAL, "total", TOTAL);


        }

        // 5) 다음 질문 생성
        int nextIndex = (int) Math.min(TOTAL, answeredCount + 1);
        String q = safeGenerate(nextIndex, answer == null ? "" : answer);
        saveQuestion(sessionId, q);

        return Map.of("done", false, "question", q, "index", nextIndex, "total", TOTAL);
    }

    private String safeGenerate(int index, String lastAnswer) {
        try {
            String q = llmService.generateNextQuestion(index, lastAnswer);
            if (q != null && !q.isBlank()) return q.trim();
        } catch (Exception e) {
            log.warn("generateNextQuestion failed: {}", e.getMessage());
        }
        // 폴백 질문(LLM 실패 시)
        String[] fb = {
                "최근에 가장 몰입했던 일은 무엇이었고, 왜 그렇게 몰입했나요?",
                "협업에서 가장 중요하게 생각하는 가치는 무엇인가요?",
                "실패를 경험했을 때 당신만의 회복 루틴은 무엇인가요?",
                "새로운 기술을 학습할 때 본인만의 전략을 설명해 주세요.",
                "성과 압박이 큰 상황에서 우선순위를 어떻게 정하나요?"
        };
        return fb[Math.min(index - 1, fb.length - 1)];
    }

    private void saveQuestion(String sessionId, String q) {
        QuestionAnswer qa = new QuestionAnswer();
        qa.setSessionId(sessionId);
        qa.setQuestionText(q);
        qaRepository.save(qa);
    }
}
