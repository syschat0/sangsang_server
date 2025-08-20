package kr.co.bnksys.sangsang.controller;

import kr.co.bnksys.sangsang.model.RecommendRequest;
import kr.co.bnksys.sangsang.model.RecommendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class RecommendController {

    private static final Logger log = LoggerFactory.getLogger(RecommendController.class);
    private static final String RECOMMEND_API_URL = "http://127.0.0.1:8888/recommend"; // python service url
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;

   /**
     * 최종 방법: 유연한 응답 처리 (배열/객체 모두 처리)
     */
    @PostMapping("/recommend")
    public ResponseEntity<?> fn_Recommand(@RequestBody RecommendRequest request) {
        try {
            log.info("추천 요청 시작 (유연한 방법): {}", request);
            
            // String으로 응답 받기
            ResponseEntity<String> response = restTemplate.postForEntity(
                RECOMMEND_API_URL, 
                request, 
                String.class
            );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String responseBody = response.getBody();
                log.debug("외부 API 원본 응답: {}", responseBody);
                
                List<RecommendResponse> results = parseFlexibleResponse(responseBody);
                
                log.info("추천 결과 {} 개를 성공적으로 받았습니다.", results.size());
                return ResponseEntity.ok(results);
                
            } else {
                log.error("외부 API 호출 실패: HTTP {}", response.getStatusCode());
                return ResponseEntity.status(response.getStatusCode())
                    .body(Map.of("error", "추천 서비스 호출에 실패했습니다."));
            }
            
        } catch (RestClientException e) {
            log.error("외부 API 연결 오류: {}", e.getMessage(), e);
            return ResponseEntity.status(503)
                .body(Map.of("error", "추천 서비스에 연결할 수 없습니다: " + e.getMessage()));
        } catch (Exception e) {
            log.error("추천 처리 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                .body(Map.of("error", "추천 처리 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    /**
     * 유연한 JSON 응답 파싱 (배열 또는 객체 처리)
     */
    private List<RecommendResponse> parseFlexibleResponse(String responseBody) {
        try {
            // JSON 응답이 배열인지 객체인지 확인
            responseBody = responseBody.trim();
            
            if (responseBody.startsWith("[")) {
                // 배열 형태의 응답
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
                // 객체 형태의 응답 - 단일 결과이거나 data 필드에 배열이 있을 수 있음
                Map<String, Object> responseMap = objectMapper.readValue(
                    responseBody,
                    new TypeReference<Map<String, Object>>() {}
                );
                
                List<RecommendResponse> results = new ArrayList<>();
                
                // 1. 직접 단일 객체인 경우
                if (responseMap.containsKey("id") || responseMap.containsKey("기관명")) {
                    results.add(mapToRecommendResponse(responseMap));
                    return results;
                }
                
                // 2. data, results, items 등의 필드에 배열이 있는 경우
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
                
                // 3. 알 수 없는 구조인 경우 로그 출력
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
     * Map 객체를 RecommendResponse 객체로 변환 (에러 처리 강화)
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
            // 빈 객체라도 반환하여 전체 처리가 중단되지 않도록 함
            RecommendResponse response = new RecommendResponse();
            response.setId(getString(map, "id"));
            response.set기관명("정보 없음");
            response.set일반전형("정보 없음");
            response.set유사도(0.0);
            return response;
        }
    }
    
    /**
     * Map에서 String 값을 안전하게 추출
     */
    private String getString(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : null;
    }
    
    /**
     * Map에서 Double 값을 안전하게 추출
     */
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
