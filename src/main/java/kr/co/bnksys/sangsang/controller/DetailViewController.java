package kr.co.bnksys.sangsang.controller;

import kr.co.bnksys.sangsang.model.JobPosting;
import kr.co.bnksys.sangsang.mapper.JobPostingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/detail")
public class DetailViewController {

    @Autowired
    private JobPostingMapper jobPostingMapper;

    /**
     * ID를 받아서 'TMP_채용공고_분리' 테이블에서 해당 ID의 채용공고를 리턴
     * @param id 채용공고 ID
     * @return 채용공고 정보 또는 404 Not Found
     */
    @GetMapping("/{id}")
    public ResponseEntity<JobPosting> getJobPostingById(@PathVariable Long id) {
        try {
            JobPosting jobPosting = jobPostingMapper.selectJobPostingById(id);
            
            if (jobPosting != null) {
                return ResponseEntity.ok(jobPosting);
            } else {
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            // 로깅을 위해 예외 출력
            System.err.println("Error retrieving job posting with id " + id + ": " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 모든 채용공고를 리턴 (테스트용)
     * @return 모든 채용공고 리스트
     */
    @GetMapping("/all")
    public ResponseEntity<List<JobPosting>> getAllJobPostings() {
        try {
            List<JobPosting> jobPostings = jobPostingMapper.selectAllJobPostings();
            return ResponseEntity.ok(jobPostings);
        } catch (Exception e) {
            System.err.println("Error retrieving all job postings: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 채용공고 개수 확인 (테스트용)
     * @return 총 채용공고 개수
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getJobPostingCount() {
        try {
            Long count = jobPostingMapper.countJobPostings();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            System.err.println("Error counting job postings: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
