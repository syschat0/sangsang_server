package kr.co.bnksys.sangsang.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.bnksys.sangsang.model.RecommendRequest;
import kr.co.bnksys.sangsang.model.RecommendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class RecommendService {

    private static final Logger log = LoggerFactory.getLogger(RecommendService.class);
    private static final String RECOMMEND_API_URL = "http://127.0.0.1:8888/recommend"; // Python 서비스 URL

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 추천 서비스 호출
     */
    public List<RecommendResponse> getRecommendations(RecommendRequest request) {
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    RECOMMEND_API_URL,
                    request,
                    String.class
            );

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                log.error("외부 API 호출 실패: HTTP {}", response.getStatusCode());
                return Collections.emptyList();
            }

            String responseBody = response.getBody();
            log.debug("외부 API 원본 응답: {}", responseBody);

            return parseFlexibleResponse(responseBody);

        } catch (Exception e) {
            log.error("추천 처리 중 오류 발생: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * 유연한 JSON 응답 파싱 (배열 또는 객체 처리)
     */
    private List<RecommendResponse> parseFlexibleResponse(String responseBody) {
        try {
            responseBody = responseBody.trim();

            if (responseBody.startsWith("[")) {
                // 배열 형태 응답
                List<Map<String, Object>> rawResults = objectMapper.readValue(
                        responseBody,
                        new TypeReference<List<Map<String, Object>>>() {}
                );

                List<RecommendResponse> results = new ArrayList<>();
                for (Map<String, Object> rawResult : rawResults) {
                    results.add(mapToRecommendResponse(rawResult));
                }
                return results;

            } else if (responseBody.startsWith("{")) {
                // 객체 형태 응답
                Map<String, Object> responseMap = objectMapper.readValue(
                        responseBody,
                        new TypeReference<Map<String, Object>>() {}
                );

                List<RecommendResponse> results = new ArrayList<>();

                // 단일 객체
                if (responseMap.containsKey("id") || responseMap.containsKey("기관명")) {
                    results.add(mapToRecommendResponse(responseMap));
                    return results;
                }

                // 배열이 특정 키 안에 있는 경우
                for (String key : new String[]{"data", "results", "items", "recommendations"}) {
                    Object dataValue = responseMap.get(key);
                    if (dataValue instanceof List) {
                        @SuppressWarnings("unchecked")
                        List<Map<String, Object>> dataList = (List<Map<String, Object>>) dataValue;
                        for (Map<String, Object> item : dataList) {
                            results.add(mapToRecommendResponse(item));
                        }
                        return results;
                    }
                }

                log.warn("알 수 없는 응답 구조: {}", responseBody);
                return new ArrayList<>();

            } else {
                log.warn("지원하지 않는 응답 형태: {}", responseBody);
                return new ArrayList<>();
            }

        } catch (Exception e) {
            log.error("응답 파싱 중 오류 발생: {}, 응답: {}", e.getMessage(), responseBody);
            return new ArrayList<>();
        }
    }

    /**
     * Map → RecommendResponse 변환
     */
    private RecommendResponse mapToRecommendResponse(Map<String, Object> map) {
        try {
            RecommendResponse response = new RecommendResponse();

            response.setId(getString(map, "id"));
            response.set기관명(getString(map, "기관명"));
            response.set일반전형(getString(map, "일반전형"));
            response.set유사도(getDouble(map, "유사도"));

            return response;
        } catch (Exception e) {
            log.warn("응답 데이터 변환 중 오류 발생: {}, 데이터: {}", e.getMessage(), map);
            RecommendResponse response = new RecommendResponse();
            response.setId(getString(map, "id"));
            response.set기관명("정보 없음");
            response.set일반전형("정보 없음");
            response.set유사도(0.0);
            return response;
        }
    }

    private String getString(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : null;
    }

    private Double getDouble(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return null;

        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }

        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            log.warn("유사도 값 변환 실패: {}", value);
            return null;
        }
    }
}
