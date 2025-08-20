package kr.co.bnksys.sangsang.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface HireInfoMapper {
    
    List<HashMap> selectAllFromTable(String tableNm);
}