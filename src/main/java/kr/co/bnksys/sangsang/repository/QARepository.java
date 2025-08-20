package kr.co.bnksys.sangsang.repository;

import kr.co.bnksys.sangsang.model.QuestionAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QARepository extends JpaRepository<QuestionAnswer, Long> {
    List<QuestionAnswer> findBySessionIdOrderByIdAsc(String sessionId);
    long countBySessionId(String sessionId);

    Optional<QuestionAnswer> findTopBySessionIdOrderByIdDesc(String sessionId);
    long countBySessionIdAndAnswerTextIsNotNull(String sessionId);

}
