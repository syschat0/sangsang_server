package kr.co.bnksys.sangsang.controller;

import kr.co.bnksys.sangsang.model.RecommendRequest;
import kr.co.bnksys.sangsang.model.RecommendResponse;
import kr.co.bnksys.sangsang.service.RecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class RecommendController {

    @Autowired
    private RecommendService recommendService;

    @PostMapping("/recommend")
    public ResponseEntity<?> fn_Recommand(@RequestBody RecommendRequest request) {
        List<RecommendResponse> results = recommendService.getRecommendations(request);

        if (results.isEmpty()) {
            return ResponseEntity.status(500).body(Map.of("error", "추천 결과가 없습니다."));
        }

        return ResponseEntity.ok(results);
    }
}
