package kr.co.bnksys.sangsang.mapper;

import kr.co.bnksys.sangsang.model.JobPosting;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface JobPostingMapper {
    
    /**
     * ID로 채용공고 조회
     * @param id 채용공고 ID
     * @return 채용공고 정보
     */
    JobPosting selectJobPostingById(@Param("id") Long id);
    
    /**
     * 모든 채용공고 조회
     * @return 모든 채용공고 리스트
     */
    List<JobPosting> selectAllJobPostings();
    
    /**
     * 채용공고 총 개수 조회
     * @return 총 개수
     */
    Long countJobPostings();
}
