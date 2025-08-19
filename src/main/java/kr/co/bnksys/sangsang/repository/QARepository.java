package kr.co.bnksys.sangsang.repository;

import kr.co.bnksys.sangsang.model.QuestionAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QARepository extends JpaRepository<QuestionAnswer, Long> {
    List<QuestionAnswer> findBySessionIdOrderByIdAsc(Long sessionId);
    long countBySessionId(Long sessionId);
}
